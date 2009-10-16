/**
 * junit-interceptors: JUnit Interceptors Collection
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 16, 2009
 */
package junit.interceptors.jpa;

import static junit.interceptors.util.Reflection.invoke;
import static junit.interceptors.util.Reflection.set;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import junit.interceptors.TestFixture;
import junit.interceptors.db.Fixtures;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.hibernate.ejb.Ejb3Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alistair A. Israel
 * @since 0.3
 */
public class HibernatePersistenceContext extends TestFixture {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernatePersistenceContext.class);

    private final List<String> fixtureNames = new ArrayList<String>();

    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    private JdbcDatabaseTester jdbcDatabaseTester;

    /**
     * @param classes
     *        the annotated classes
     */
    public HibernatePersistenceContext(final Class<?>... classes) {
        try {
            DriverManager.getConnection("jdbc:derby:target/DerbyDB;create=true");
        } catch (final SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        final Properties properties = new Properties();
        properties.put("hibernate.connection.url", "jdbc:derby:target/DerbyDB");
        properties.put("hibernate.connection.pool_size", "5");
        properties.put("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");

        final Ejb3Configuration cfg = new Ejb3Configuration();
        cfg.configure("test", properties);
        for (final Class<?> clazz : classes) {
            cfg.addAnnotatedClass(clazz);
        }

        entityManagerFactory = cfg.buildEntityManagerFactory();
        entityManager = entityManagerFactory.createEntityManager();
    }

    /**
     * @param object
     *        an object to which we will apply EJB 3.0 style @PersistenceContext and @PostConstruct handling
     */
    public final void injectAndPostConstruct(final Object object) {
        final Class<? extends Object> clazz = object.getClass();
        for (final Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(PersistenceContext.class)) {
                final Class<?> type = field.getType();
                if (type.equals(EntityManager.class)) {
                    set(field).of(object).to(entityManager);
                } else {
                    LOGGER.warn("Found field \"{}\" annotated with @PersistenceContext "
                            + "but is of type {}", field.getName(), type.getName());
                }
            }
        }

        for (final Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                final int nParameters = method.getParameterTypes().length;
                if (nParameters == 0) {
                    invoke(method).on(object);
                } else {
                    LOGGER.warn("Found method \"{}\" annotated @PostConstruct "
                            + "but don't know how to invoke with {} parameters", method.getName(),
                            nParameters);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.interceptors.TestFixture#inspect(java.lang.Object, java.lang.reflect.Method)
     */
    @Override
    protected final void inspect(final Object target, final Method method) {
        final Class<? extends Object> targetClass = target.getClass();
        if (targetClass.isAnnotationPresent(Fixtures.class)) {
            addFixturesFromAnnotation(targetClass.getAnnotation(Fixtures.class));
        }
        if (method.isAnnotationPresent(Fixtures.class)) {
            addFixturesFromAnnotation(method.getAnnotation(Fixtures.class));
        }
    }

    /**
     * @param annotation
     *        the {@link Fixtures} annotation
     */
    private void addFixturesFromAnnotation(final Fixtures annotation) {
        for (final String fixtureName : annotation.value()) {
            fixtureNames.add(fixtureName);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.interceptors.TestFixture#setUp()
     */
    @Override
    protected final void setUp() throws Throwable {
        jdbcDatabaseTester = new JdbcDatabaseTester(EmbeddedDriver.class.getName(),
                "jdbc:derby:target/DerbyDB");
        if (fixtureNames.isEmpty()) {
            LOGGER.warn("No fixtures to load! Specify fixtures using @Fixtures.");
        } else {
            loadFixtures();
        }

        jdbcDatabaseTester.onSetup();
    }

    /**
     * @throws DataSetException
     *         on any exception
     */
    private void loadFixtures() throws DataSetException {
        final List<IDataSet> dataSets = new ArrayList<IDataSet>();

        for (final String fixtureName : fixtureNames) {
            LOGGER.trace("Attempting to load database fixture \"" + fixtureName + "\"");
            final IDataSet dataSet = attemptToLoadFixture(fixtureName);
            if (dataSet != null) {
                dataSets.add(dataSet);
            }
        }

        if (dataSets.isEmpty()) {
            LOGGER.warn("Found 0 data sets!");
        } else {
            final CompositeDataSet compositeDataSet = new CompositeDataSet(dataSets
                    .toArray(new IDataSet[dataSets.size()]));
            jdbcDatabaseTester.setDataSet(compositeDataSet);
        }
    }

    /**
     * @param fixtureName
     *        the fixture name
     * @return {@link IDataSet}
     */
    private IDataSet attemptToLoadFixture(final String fixtureName) {
        IDataSet dataSet = null;

        try {
            final InputStream in = ClassLoader.getSystemResourceAsStream(fixtureName);
            try {
                if (in != null) {
                    if (fixtureName.endsWith(".xml")) {
                        dataSet = new XmlDataSet(in);
                    }
                }
            } finally {
                in.close();
            }
        } catch (final Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return dataSet;
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.interceptors.TestFixture#tearDown()
     */
    @Override
    protected final void tearDown() throws Throwable {
        jdbcDatabaseTester.onTearDown();
        entityManagerFactory.close();
        // shutdown Derby
        // try {
        // DriverManager.getConnection("jdbc:derby:test;shutdown=true");
        // } catch (final SQLException e) {
        // if (e.getErrorCode() == 50000 && "XJ015".equals(e.getSQLState())) {
        // LOGGER.info("Derby shut down normally");
        // } else {
        // // if the error code or SQLState is different, we have
        // // an unexpected exception (shutdown failed)
        // throw e;
        // }
        // }
    }

    /**
     * @return {@link EntityManager}
     * @see javax.persistence.EntityManagerFactory#createEntityManager()
     */
    public final EntityManager getEntityManager() {
        return this.entityManager;
    }

}

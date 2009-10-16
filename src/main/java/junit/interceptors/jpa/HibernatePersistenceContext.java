/**
 * junit-interceptors: JUnit Interceptors Collection
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 16, 2009
 */
package junit.interceptors.jpa;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.interceptors.TestFixture;
import junit.interceptors.db.Fixtures;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.hibernate.ejb.HibernatePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alistair A. Israel
 */
public class HibernatePersistenceContext extends TestFixture {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernatePersistenceContext.class);

    private final List<String> fixtureNames = new ArrayList<String>();

    private EntityManagerFactory entityManagerFactory;

    /**
     * {@inheritDoc}
     *
     * @see junit.interceptors.TestFixture#inspect(java.lang.Object,
     *      java.lang.reflect.Method)
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
        final Properties properties = new Properties();
        properties.put("hibernate.connection.url", "jdbc:derby:test;create=true");
        properties.put("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");

        entityManagerFactory = new HibernatePersistence().createEntityManagerFactory("test", properties);

        final JdbcDatabaseTester jdbcDatabaseTester = new JdbcDatabaseTester(
                EmbeddedDriver.class.getName(), "jdbc:derby:test;create=true");
        final List<IDataSet> dataSets = new ArrayList<IDataSet>();
        for (final String fixtureName : fixtureNames) {
            LOGGER.trace("Attempting to load database fixture \"" + fixtureName + "\"");
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

            if (dataSet != null) {
                dataSets.add(dataSet);
            }
        }

        final CompositeDataSet compositeDataSet = new CompositeDataSet(dataSets
                .toArray(new IDataSet[dataSets.size()]));
        jdbcDatabaseTester.setDataSet(compositeDataSet);

        jdbcDatabaseTester.onSetup();
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.interceptors.TestFixture#tearDown()
     */
    @Override
    protected final void tearDown() throws Throwable {
        DriverManager.getDriver("jdbc:derby:test;shutdown=true");
    }

    /**
     * @return {@link EntityManager}
     * @see javax.persistence.EntityManagerFactory#createEntityManager()
     */
    public final EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

}

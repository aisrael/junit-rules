/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 22, 2009
 */
package junit.rules.jpa.hibernate;

import static junit.rules.jpa.hibernate.DerbyHibernateUtil.JDBC_DERBY_URL;
import static junit.rules.util.Reflection.invoke;
import static junit.rules.util.Reflection.set;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import junit.rules.dbunit.DbUnitUtil;
import junit.rules.dbunit.FixturesUtil;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.hibernate.ejb.Ejb3Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alistair A. Israel
 */
public class DerbyHibernateTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernatePersistenceContext.class);

    private static final ThreadLocal<EntityManagerFactory> ENTITY_MANAGER_FACTORY = new ThreadLocal<EntityManagerFactory>();

    private EntityManager entityManager;

    private JdbcDatabaseTester jdbcDatabaseTester;

    /**
     *
     */
    @BeforeClass
    public static void initializeDerbyHibernate() {
        final Ejb3Configuration configuration = DerbyHibernateUtil.configureDerbyHibernateJpa();
        ENTITY_MANAGER_FACTORY.set(configuration.buildEntityManagerFactory());
    }

    /**
     *
     */
    @AfterClass
    public static void closeHibernateDerby() {
        ENTITY_MANAGER_FACTORY.get().close();
        try {
            DriverManager.getConnection(JDBC_DERBY_URL + ";shutdown=true");
        } catch (final SQLException e) {
            if (e.getErrorCode() == 50000 && "XJ015".equals(e.getSQLState())) {
                LOGGER.info("Derby shut down normally");
            } else {
                // if the error code or SQLState is different, we have
                // an unexpected exception (shutdown failed)
                throw new RuntimeException(e.getMessage(), e);
            }
        }
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
                    LOGGER.warn("Found field \"{}\" annotated with @PersistenceContext " + "but is of type {}", field
                            .getName(), type.getName());
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
                            + "but don't know how to invoke with {} parameters", method.getName(), nParameters);
                }
            }
        }
    }

    /**
     * @throws Throwable
     *         on any throwable
     */
    @Before
    public final void initializeDbUnit() throws Throwable {
        entityManager = ENTITY_MANAGER_FACTORY.get().createEntityManager();

        jdbcDatabaseTester = new JdbcDatabaseTester(EmbeddedDriver.class.getName(), JDBC_DERBY_URL);
        final List<String> fixtureNames = FixturesUtil.getFixtureNames(getClass());
        if (fixtureNames.isEmpty()) {
            LOGGER.warn("No fixtures to load! Specify fixtures using @Fixtures.");
        } else {
            loadFixtures(fixtureNames);
        }

        jdbcDatabaseTester.onSetup();
    }

    /**
     * @param fixtureNames
     *        the fixture names
     * @throws DataSetException
     *         on any exception
     */
    private void loadFixtures(final List<String> fixtureNames) throws DataSetException {
        final IDataSet[] dataSets = DbUnitUtil.loadDataSets(fixtureNames);

        if (dataSets.length == 0) {
            LOGGER.warn("Found 0 data sets!");
        } else {
            final CompositeDataSet compositeDataSet = new CompositeDataSet(dataSets);
            jdbcDatabaseTester.setDataSet(compositeDataSet);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.rules.TestFixture#tearDown()
     */
    @After
    public final void tearDownDbUnit() throws Throwable {
        jdbcDatabaseTester.onTearDown();
    }

    /**
     * @return {@link EntityManager}
     * @see javax.persistence.EntityManagerFactory#createEntityManager()
     */
    public final EntityManager getEntityManager() {
        return this.entityManager;
    }

}

/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 16, 2009
 */
package junit.rules.jpa.hibernate;

import static junit.rules.util.Reflection.invoke;
import static junit.rules.util.Reflection.set;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.hibernate.ejb.Ejb3Configuration;
import org.junit.rules.ClassRule;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alistair A. Israel
 * @since 0.3
 */
public class HibernatePersistenceContext implements ClassRule, junit.rules.jpa.PersistenceContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernatePersistenceContext.class);

    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

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
     * {@inheritDoc}
     *
     * @see org.junit.rules.ClassRule#apply(org.junit.runners.model.Statement, org.junit.runners.model.TestClass)
     */
    @Override
    public final Statement apply(final Statement base, final TestClass testClass) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
                try {
                    DriverManager.getConnection("jdbc:derby:;shutdown=true");
                } catch (final SQLException e) {
                    if (e.getErrorCode() == 50000 && "XJ015".equals(e.getSQLState())) {
                        LOGGER.info("Derby shut down normally");
                    } else {
                        // if the error code or SQLState is different, we have
                        // an unexpected exception (shutdown failed)
                        throw e;
                    }
                }
            }

        };
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
     * @return {@link EntityManager}
     * @see javax.persistence.EntityManagerFactory#createEntityManager()
     */
    public final EntityManager getEntityManager() {
        return this.entityManager;
    }
}

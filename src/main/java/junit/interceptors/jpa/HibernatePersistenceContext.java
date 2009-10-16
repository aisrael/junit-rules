/**
 * junit-interceptors: JUnit Interceptors Collection
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 16, 2009
 */
package junit.interceptors.jpa;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.interceptors.TestFixture;

import org.hibernate.ejb.HibernatePersistence;

/**
 * @author Alistair A. Israel
 */
public class HibernatePersistenceContext extends TestFixture {

    private EntityManagerFactory entityManagerFactory;

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
    }

    /**
     * @return {@link EntityManager}
     * @see javax.persistence.EntityManagerFactory#createEntityManager()
     */
    public final EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

}

/**
 * junit-interceptors: JUnit Interceptors Collection
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 15, 2009
 */
package junit.interceptors.jpa;

import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.ejb.HibernatePersistence;

/**
 * @author Alistair A. Israel
 */
public class TestPersistenceProvider implements PersistenceProvider {

    /**
     * {@inheritDoc}
     *
     * @see javax.persistence.spi.PersistenceProvider#createContainerEntityManagerFactory(javax.persistence.spi.PersistenceUnitInfo,
     *      java.util.Map)
     */
    @Override
    public EntityManagerFactory createContainerEntityManagerFactory(final PersistenceUnitInfo info,
            final Map map) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.persistence.spi.PersistenceProvider#createEntityManagerFactory(java.lang.String,
     *      java.util.Map)
     */
    @Override
    public EntityManagerFactory createEntityManagerFactory(final String emName, final Map map) {
        final Properties properties = new Properties();
        if (map != null && !map.isEmpty()) {
            for (final Object key : map.keySet()) {
                properties.put(key, map.get(key));
            }
        }
        properties.put("hibernate.connection.url", "jdbc:derby:test;create=true");
        properties.put("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        return new HibernatePersistence().createEntityManagerFactory(emName, map);
    }

}

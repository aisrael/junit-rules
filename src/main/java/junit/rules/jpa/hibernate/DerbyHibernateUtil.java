/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 22, 2009
 */
package junit.rules.jpa.hibernate;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.ejb.Ejb3Configuration;

/**
 * @author Alistair A. Israel
 */
public final class DerbyHibernateUtil {

    /**
     * {@value #JDBC_DERBY_URL}
     */
    public static final String JDBC_DERBY_URL = "jdbc:derby:test";

    /**
     *
     */
    private DerbyHibernateUtil() {
        // noop
    }

    /**
     * @param classes
     *        the annotated classes
     * @return {@link Ejb3Configuration}
     */
    public static Ejb3Configuration configureDerbyHibernateJpa(final Class<?>... classes) {
        try {
            DriverManager.getConnection(JDBC_DERBY_URL + ";create=true");
        } catch (final SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        final Properties properties = new Properties();
        properties.put("hibernate.connection.url", JDBC_DERBY_URL);
        properties.put("hibernate.connection.pool_size", "5");
        properties.put("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");

        final Ejb3Configuration cfg = new Ejb3Configuration();
        cfg.configure("test", properties);
        for (final Class<?> clazz : classes) {
            cfg.addAnnotatedClass(clazz);
        }
        return cfg;
    }

}

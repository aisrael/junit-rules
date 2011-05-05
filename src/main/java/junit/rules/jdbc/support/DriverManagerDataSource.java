/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created May 5, 2011
 */
package junit.rules.jdbc.support;

import static junit.rules.util.ObjectUtils.nullSafeEquals;
import static junit.rules.util.StringUtils.hasLength;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.sql.DataSource;

/**
 * Poor man's implementation of Spring's DriverManager-backed {@link DataSource}.
 *
 * @author Alistair.Israel
 */
public class DriverManagerDataSource implements DataSource {

    private boolean needToRegisterDriver;

    private String jdbcDriver;

    private String jdbcUrl;

    private String jdbcUser;

    private String jdbcPassword;

    /**
     * @return the jdbcDriver
     */
    public final String getJdbcDriver() {
        return jdbcDriver;
    }

    /**
     * @param jdbcDriver
     *        the jdbcDriver to set
     */
    public final void setJdbcDriver(final String jdbcDriver) {
        if (!nullSafeEquals(this.jdbcDriver, jdbcDriver)) {
            this.jdbcDriver = jdbcDriver;
            needToRegisterDriver = true;
        }
    }

    /**
     * @return the jdbcUrl
     */
    public final String getJdbcUrl() {
        return jdbcUrl;
    }

    /**
     * @param jdbcUrl
     *        the jdbcUrl to set
     */
    public final void setJdbcUrl(final String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * @return the jdbcUser
     */
    public final String getJdbcUser() {
        return jdbcUser;
    }

    /**
     * @param jdbcUser
     *        the jdbcUser to set
     */
    public final void setJdbcUser(final String jdbcUser) {
        this.jdbcUser = jdbcUser;
    }

    /**
     * @return the jdbcPassword
     */
    public final String getJdbcPassword() {
        return jdbcPassword;
    }

    /**
     * @param jdbcPassword
     *        the jdbcPassword to set
     */
    public final void setJdbcPassword(final String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.sql.CommonDataSource#getLogWriter()
     */
    @Override
    public final PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.sql.CommonDataSource#getLoginTimeout()
     */
    @Override
    public final int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
     */
    @Override
    public final void setLogWriter(final PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.sql.CommonDataSource#setLoginTimeout(int)
     */
    @Override
    public final void setLoginTimeout(final int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    @Override
    public final boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return DataSource.class.equals(iface);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public final <T> T unwrap(final Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return (T) this;
        }
        throw new SQLException("DriverManagerDataSource can only be unwrapped as javax.sql.DataSource, not as "
                + iface.getCanonicalName());
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.sql.DataSource#getConnection()
     */
    @Override
    public final Connection getConnection() throws SQLException {
        if (needToRegisterDriver) {
            registerDriver();
        }
        if (hasLength(jdbcUser) || jdbcPassword != null) {
            return getConnection(jdbcUser, jdbcPassword);
        }
        return DriverManager.getConnection(jdbcUrl);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
     */
    @Override
    public final Connection getConnection(final String username, final String password) throws SQLException {
        if (needToRegisterDriver) {
            registerDriver();
        }
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    /**
     * Register the JDBC driver.
     */
    private void registerDriver() {
        for (final Enumeration<Driver> drivers = DriverManager.getDrivers(); drivers.hasMoreElements();) {
            final Driver driver = drivers.nextElement();
            if (driver.getClass().getCanonicalName().equals(jdbcDriver)) {
                needToRegisterDriver = false;
                return;
            }
        }
        try {
            final Class<?> driverClass = Class.forName(jdbcDriver);
            if (!Driver.class.isAssignableFrom(driverClass)) {
                throw new RuntimeException("Driver " + driverClass + " does not implement "
                        + Driver.class.getCanonicalName() + "!");
            }
            needToRegisterDriver = false;
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException("ClassNotFoundException attempting to load driver class " + jdbcDriver + "!", e);
        }
    }
}

/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created May 5, 2011
 */
package junit.rules.derby;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import junit.rules.TestFixture;
import junit.rules.jdbc.support.DriverManagerDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It's a JUnit {@link org.junit.Rule} that also masquerades as a JDBC {@link DataSource}.
 *
 * @author Alistair.Israel
 */
public class DerbyDataSourceRule extends TestFixture implements DataSource {

    private static final Logger logger = LoggerFactory.getLogger(DerbyDataSourceRule.class);

    private final String databaseName;

    private DataSource dataSource;

    /**
     * Instantiates a new Derby database test fixture.
     *
     * @param databaseName
     *        the database name
     */
    public DerbyDataSourceRule(final String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Instantiates a new Derby database test fixture with the default name ({@code "test"}).
     */
    public DerbyDataSourceRule() {
        this("test");
    }

    /**
     * Setup Derby
     *
     * @throws Throwable
     *         if setup fails
     */
    @Override
    protected final void setUp() throws Throwable {
        logger.debug("setUp()");
        final String jdbcUrl = constructJdbcUrl();
        logger.debug("Using JDBC URL: " + jdbcUrl);
        DriverManager.getConnection(jdbcUrl);

        final DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setJdbcUrl(jdbcUrl);
        dataSource = ds;
        logger.info("Initialized Derby database at \"" + jdbcUrl + "\"");
    }

    /**
     * @return the JDBC URL to use
     */
    private String constructJdbcUrl() {
        return "jdbc:derby:memory:" + databaseName + ";create=true";
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.sql.DataSource#getConnection()
     */
    @Override
    public final Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
     */
    @Override
    public final Connection getConnection(final String arg0, final String arg1) throws SQLException {
        return dataSource.getConnection(arg0, arg1);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.sql.CommonDataSource#getLogWriter()
     */
    @Override
    public final PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.sql.CommonDataSource#getLoginTimeout()
     */
    @Override
    public final int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    @Override
    public final boolean isWrapperFor(final Class<?> arg0) throws SQLException {
        return dataSource.isWrapperFor(arg0);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
     */
    @Override
    public final void setLogWriter(final PrintWriter arg0) throws SQLException {
        dataSource.setLogWriter(arg0);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.sql.CommonDataSource#setLoginTimeout(int)
     */
    @Override
    public final void setLoginTimeout(final int arg0) throws SQLException {
        dataSource.setLoginTimeout(arg0);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    @Override
    public final <T> T unwrap(final Class<T> arg0) throws SQLException {
        return dataSource.unwrap(arg0);
    }

    /**
     * Don't know where else to put it for now.
     *
     * @param tableName
     *        the table name to count
     * @return the count
     */
    public final int count(final String tableName) {
        try {
            final Connection conn = dataSource.getConnection();
            try {
                final PreparedStatement ps = conn.prepareStatement("SELECT count(*) FROM " + tableName);
                try {
                    final ResultSet rs = ps.executeQuery();
                    try {
                        rs.next();
                        return rs.getInt(1);
                    } finally {
                        rs.close();
                    }
                } finally {
                    ps.close();
                }
            } finally {
                conn.close();
            }
        } catch (final SQLException e) {
            throw new AssertionError(e);
        }
    }

}

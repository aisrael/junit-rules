/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 19, 2009
 */
package junit.rules.dbunit;

import static junit.rules.dbunit.FixturesUtil.getFixtureNames;

import java.lang.reflect.Method;
import java.util.List;

import junit.rules.TestFixture;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.dbunit.JdbcDatabaseTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alistair A. Israel
 */
public class DbUnitTestFixtures extends TestFixture {

    private static final Logger logger = LoggerFactory.getLogger(DbUnitTestFixtures.class);

    private List<String> fixtureNames;

    private JdbcDatabaseTester jdbcDatabaseTester;

    /**
     * {@inheritDoc}
     *
     * @see junit.rules.TestFixture#inspect(java.lang.Object, java.lang.reflect.Method)
     */
    @Override
    protected final void inspect(final Object target, final Method method) {
        fixtureNames = getFixtureNames(target.getClass(), method);
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.rules.TestFixture#setUp()
     */
    @Override
    protected final void setUp() throws Throwable {
        jdbcDatabaseTester = new JdbcDatabaseTester(EmbeddedDriver.class.getName(), "jdbc:derby:test");
        if (fixtureNames.isEmpty()) {
            logger.warn("No fixtures to load! Specify fixtures using @Fixtures.");
        } else {
            DbUnitUtil.loadDataSets(fixtureNames);
        }

        jdbcDatabaseTester.onSetup();
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.rules.TestFixture#tearDown()
     */
    @Override
    protected final void tearDown() throws Throwable {
        jdbcDatabaseTester.onTearDown();
    }

}

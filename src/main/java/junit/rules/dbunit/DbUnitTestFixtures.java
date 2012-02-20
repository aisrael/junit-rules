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
import junit.rules.util.Reflection;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alistair A. Israel
 */
public class DbUnitTestFixtures extends TestFixture {

    private static final Logger logger = LoggerFactory.getLogger(DbUnitTestFixtures.class);

    private List<String> fixtureNames;

    private JdbcDatabaseTester tester;

    /**
     * Default constructor. Will use a hard-coded Derby 'test' database.
     */
    public DbUnitTestFixtures() {
        // noop
    }

    /**
     * @param jdbcDatabaseTester
     *        the {@link JdbcDatabaseTester} to use
     */
    public DbUnitTestFixtures(final JdbcDatabaseTester jdbcDatabaseTester) {
        this.tester = jdbcDatabaseTester;
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.rules.TestFixture#inspect(org.junit.runner.Description)
     */
    @Override
    protected final void inspect(final Description description) {
        final Class<?> testClass = description.getTestClass();
        final Method method = Reflection.quietlyGetMethod(testClass, description.getMethodName());
        fixtureNames = getFixtureNames(testClass, method);
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.rules.TestFixture#setUp()
     */
    @Override
    protected final void setUp() throws Throwable {
        if (tester == null) {
            tester = new JdbcDatabaseTester(EmbeddedDriver.class.getName(), "jdbc:derby:test");
        }
        if (fixtureNames.isEmpty()) {
            logger.warn("No fixtures to load! Specify fixtures using @Fixtures.");
        } else {
            try {
                final IDataSet[] dataSets = DbUnitUtil.loadDataSets(fixtureNames);
                final CompositeDataSet compositeDataSet = new CompositeDataSet(dataSets);
                tester.setDataSet(compositeDataSet);
            } catch (final DataSetException e) {
                throw new Error(e.getMessage(), e);
            }
        }

        tester.onSetup();
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.rules.TestFixture#tearDown()
     */
    @Override
    protected final void tearDown() throws Throwable {
        tester.onTearDown();
    }

}

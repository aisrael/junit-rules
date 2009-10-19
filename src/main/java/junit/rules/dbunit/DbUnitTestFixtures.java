/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 19, 2009
 */
package junit.rules.dbunit;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.rules.TestFixture;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alistair A. Israel
 */
public class DbUnitTestFixtures extends TestFixture {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbUnitTestFixtures.class);

    private final List<String> fixtureNames = new ArrayList<String>();

    private JdbcDatabaseTester jdbcDatabaseTester;

    /**
     * {@inheritDoc}
     *
     * @see junit.rules.TestFixture#inspect(java.lang.Object, java.lang.reflect.Method)
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
     * @see junit.rules.TestFixture#setUp()
     */
    @Override
    protected final void setUp() throws Throwable {
        jdbcDatabaseTester = new JdbcDatabaseTester(EmbeddedDriver.class.getName(), "jdbc:derby:target/DerbyDB");
        if (fixtureNames.isEmpty()) {
            LOGGER.warn("No fixtures to load! Specify fixtures using @Fixtures.");
        } else {
            loadFixtures();
        }

        jdbcDatabaseTester.onSetup();
    }

    /**
     * @throws DataSetException
     *         on any exception
     */
    private void loadFixtures() throws DataSetException {
        final List<IDataSet> dataSets = new ArrayList<IDataSet>();

        for (final String fixtureName : fixtureNames) {
            LOGGER.trace("Attempting to load database fixture \"" + fixtureName + "\"");
            final IDataSet dataSet = attemptToLoadFixture(fixtureName);
            if (dataSet != null) {
                dataSets.add(dataSet);
            }
        }

        if (dataSets.isEmpty()) {
            LOGGER.warn("Found 0 data sets!");
        } else {
            final CompositeDataSet compositeDataSet = new CompositeDataSet(dataSets.toArray(new IDataSet[dataSets
                    .size()]));
            jdbcDatabaseTester.setDataSet(compositeDataSet);
        }
    }

    /**
     * @param fixtureName
     *        the fixture name
     * @return {@link IDataSet}
     */
    private IDataSet attemptToLoadFixture(final String fixtureName) {
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
        return dataSet;
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

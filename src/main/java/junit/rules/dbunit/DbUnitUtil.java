/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 22, 2009
 */
package junit.rules.dbunit;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alistair A. Israel
 */
public final class DbUnitUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbUnitUtil.class);

    /**
     * Utility classes should not have a public or default constructor.
     */
    private DbUnitUtil() {
        // noop
    }

    /**
     * @param fixtureNames
     *        the fixture names
     * @return IDataSet[]
     */
    public static IDataSet[] loadDataSets(final List<String> fixtureNames) {
        final List<IDataSet> dataSets = new ArrayList<IDataSet>();

        for (final String fixtureName : fixtureNames) {
            LOGGER.trace("Attempting to load database fixture \"" + fixtureName + "\"");
            final IDataSet dataSet = attemptToLoadFixture(fixtureName);
            if (dataSet != null) {
                dataSets.add(dataSet);
            }
        }

        return dataSets.toArray(new IDataSet[dataSets.size()]);
    }

    /**
     * @param fixtureName
     *        the fixture name
     * @return {@link IDataSet}
     */
    private static IDataSet attemptToLoadFixture(final String fixtureName) {
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

}

/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created May 5, 2011
 */
package junit.rules.derby;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Rule;
import org.junit.Test;

/**
 * JUnit test for {@link DerbyDataSourceRule}.
 *
 * @author Alistair A. Israel
 */
public final class DerbyDataSourceRuleTest {

    // CHECKSTYLE:OFF
    @Rule
    public final DerbyDataSourceRule derby = new DerbyDataSourceRule("test");
    // CHECKSTYLE:ON

    /**
     * @throws Exception
     *         should never happen
     */
    @Test
    public void testDerbyDataSourceRule() throws Exception {
        final Connection conn = derby.getConnection();
        assertNotNull(conn);
        try {
            final Statement ps = conn.createStatement();
            try {
                final ResultSet rs = ps.executeQuery("VALUES CURRENT_DATE");
                rs.next();
                final Date i = rs.getDate(1);
                System.out.println(i);
            } finally {
                ps.close();
            }
        } finally {
            conn.close();
        }
    }
}

/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Jun 2, 2011
 */
package junit.rules.jndi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * JUnit test for {@link StubJndiContext}.
 *
 * @author Alistair A. Israel
 */
public final class StubJndiContextTest {

    /**
     * Test using {@link StubJndiContext}
     */
    public static final class TestUsingStubJndi {

        /**
         * The {@link StubJndiContext} rule
         */
        @Rule
        // SUPPRESS CHECKSTYLE VisibilityModifier
        public StubJndiContext stubJndiContext = new StubJndiContext();

        /**
         *
         */
        @Before
        public void setUp() {
            // bind a DataSource proxy that does nothing
            stubJndiContext.bind("jdbc/dataSource", Proxy.newProxyInstance(DataSource.class.getClassLoader(),
                    new Class[] { DataSource.class }, new InvocationHandler() {

                        @Override
                        public Object invoke(final Object proxy, final Method method, final Object[] args)
                                throws Throwable {
                            throw new UnsupportedOperationException("Not yet implemented");
                        }
                    }));
        }

        /**
         * @throws Exception
         *         should never happen
         */
        @Test
        public void testJndi() throws Exception {
            final InitialContext ic = new InitialContext();
            assertNotNull(ic);
            final Object obj = ic.lookup("jdbc/dataSource");
            assertNotNull(obj);
            assertTrue(obj instanceof DataSource);
        }
    }

    /**
     *
     */
    @Test
    public void testUsingStubJndi() {
        final Result result = JUnitCore.runClasses(TestUsingStubJndi.class);
        assertEquals(0, result.getFailureCount());
    }
}

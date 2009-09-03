/**
 * junit-interceptors: JUnit Interceptors Collection
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Sep 3, 2009
 */
package junit.interceptors;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * @author Alistair A. Israel
 */
public class TestFixture implements MethodRule {

    /**
     * {@inheritDoc}
     *
     * @see org.junit.rules.MethodRule#apply(org.junit.runners.model.Statement,
     *      org.junit.runners.model.FrameworkMethod, java.lang.Object)
     */
    @Override
    public final Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                setUp();
                try {
                    base.evaluate();
                } finally {
                    tearDown();
                }
            }
        };
    }

    /**
     * Override to set up your specific external resource.
     *
     * @throws Throwable
     *         if setup fails
     */
    protected void setUp() throws Throwable {
        // do nothing
    }

    /**
     * Override to tear down your specific external resource.
     *
     * @throws Throwable
     *         if teardown fails
     */
    protected void tearDown() throws Throwable {
        // do nothing
    }
}

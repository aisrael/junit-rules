/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Sep 3, 2009
 */
package junit.rules;

import java.lang.reflect.Method;

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
     * @see org.junit.rules.MethodRule#apply(org.junit.runners.model.Statement, org.junit.runners.model.FrameworkMethod,
     *      java.lang.Object)
     */
    @Override
    public final Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        inspect(target, method.getMethod());
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
     * Override to perform any reflection/introspection on the target test instance before setUp() / tearDown().
     *
     * @param target
     *        the target (test) object
     * @param method
     *        the target test method
     */
    @SuppressWarnings("unused")
    protected void inspect(final Object target, final Method method) {
    }

    /**
     * Override to set up your specific external resource.
     *
     * @throws Throwable
     *         if setup fails
     */
    protected void setUp() throws Throwable {
    }

    /**
     * Override to tear down your specific external resource.
     *
     * @throws Throwable
     *         if teardown fails
     */
    protected void tearDown() throws Throwable {
    }
}

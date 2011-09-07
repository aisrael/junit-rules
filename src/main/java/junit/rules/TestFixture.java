/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Sep 3, 2009
 */
package junit.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Alistair A. Israel
 */
public class TestFixture implements TestRule {

    /**
     * {@inheritDoc}
     *
     * @see org.junit.rules.MethodRule#apply(org.junit.runners.model.Statement, org.junit.runners.model.FrameworkMethod,
     *      java.lang.Object)
     */
    @Override
    public final Statement apply(final Statement base, final Description description) {
        inspect(description);
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
     * @param description the {@link Description}
     */
    protected void inspect(final Description description) {
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

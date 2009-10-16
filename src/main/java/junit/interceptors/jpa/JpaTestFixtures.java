/**
 * junit-interceptors: JUnit Interceptors Collection
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 15, 2009
 */
package junit.interceptors.jpa;

import junit.interceptors.TestFixture;

/**
 * @author Alistair A. Israel
 */
public class JpaTestFixtures extends TestFixture {

    /**
     * {@inheritDoc}
     *
     * @see junit.interceptors.TestFixture#inspect(java.lang.Object)
     */
    @Override
    protected final void inspect(final Object target) {
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.interceptors.TestFixture#setUp()
     */
    @Override
    protected final void setUp() throws Throwable {
        // TODO Auto-generated method stub
        super.setUp();
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.interceptors.TestFixture#tearDown()
     */
    @Override
    protected final void tearDown() throws Throwable {
        // TODO Auto-generated method stub
        super.tearDown();
    }
}

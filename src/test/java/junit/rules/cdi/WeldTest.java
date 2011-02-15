/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Feb 15, 2011
 */
package junit.rules.cdi;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.JUnitCore;

/**
 * The Class WeldTest.
 *
 * @author Alistair A. Israel
 */
public final class WeldTest {

    /**
     * Illustrates how one would use the Weld {@code @Rule}.
     *
     * @author Alistair A. Israel
     */
    public static final class UsesWeldTest {

        /** The weld. */
        @Rule
        // CHECKSTYLE:OFF
        public Weld weld = new Weld();
        // CHECKSTYLE:ON

        @Inject
        private ClassUnderTest classUnderTest;

        /**
         * Test injection.
         */
        @Test
        public void testInjection() {
            assertNotNull(classUnderTest);
            assertNotNull(classUnderTest.dependency);
        }
    }

    /**
     * Test Weld {@code @Rule} using {@link UsesWeldTest}.
     */
    @Test
    public void testWeld() {
        JUnitCore.runClasses(UsesWeldTest.class);
    }

    /**
     * The class ClassUnderTest.
     */
    public static class ClassUnderTest {
        @Inject
        private Dependency dependency;

    }

    /**
     * The interface Dependency.
     */
    public static interface Dependency {

    }

    /**
     * The default implementation of Dependency.
     */
    public static class DependencyImpl implements Dependency {

    }
}

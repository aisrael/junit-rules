/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Sep 5, 2011
 */
package junit.rules;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import junit.framework.AssertionFailedError;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * JUnit test for {@link ExpectedExceptions}
 *
 * @author Alistair A. Israel
 */
public final class ExpectedExceptionsTest {

    /**
     * Run test using {@link UsesExpectedExceptions}
     */
    @Test
    public void testExpectedExceptions() {
        runTestClassExpectOneFailure(UsesExpectedExceptions.class, new FailureCallback() {
            @Override
            public void callback(final Failure failure) {
                final Throwable e = failure.getException();
                assertNotNull(e);
                assertTrue(e instanceof ArrayIndexOutOfBoundsException);
            }
        });
    }

    /**
     * Run test {@link ExpectsExceptionButNoneThrown}
     */
    @Test
    public void testExpectsExceptionButNoneThrown() {
        runTestClassExpectOneFailure(ExpectsExceptionButNoneThrown.class, new FailureCallback() {
            @Override
            public void callback(final Failure failure) {
                final Throwable e = failure.getException();
                assertNotNull(e);
                assertTrue(e instanceof AssertionFailedError);
                assertEquals("Expected exception java.lang.NullPointerException not thrown!", e.getMessage());
            }
        });
    }

    /**
     * Expects one failure.
     *
     * @author Alistair A. Israel
     */
    private interface FailureCallback {

        /**
         * @param failure
         *        the Failure
         */
        void callback(Failure failure);
    }

    /**
     * @param testClass
     *        the test class
     * @param callback
     *        the {@link FailureCallback}
     */
    private static void runTestClassExpectOneFailure(final Class<?> testClass, final FailureCallback callback) {
        final Result result = JUnitCore.runClasses(testClass);
        final int failureCount = result.getFailureCount();
        if (failureCount == 1) {
            final Failure failure = result.getFailures().get(0);
            callback.callback(failure);
        } else {
            System.out.println("Encountered " + failureCount + " failures, expecting only 1");
            for (final Failure failure : result.getFailures()) {
                System.out.println(failure);
                final Throwable e = failure.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
            fail("Encountered " + failureCount + " failures, expecting only 1");
        }
    }

    /**
     * A sample unit test that uses {@link ExpectedExceptions} rule.
     *
     * @author Alistair A. Israel
     */
    public static final class UsesExpectedExceptions {

        /**
         * The rule
         */
        @Rule
        // SUPPRESS CHECKSTYLE VisibilityModifier
        public final ExpectedExceptions expectedExceptions = new ExpectedExceptions();

        /**
         * No exception and no annotation
         */
        @Test
        public void noException() {
            assertTrue("Should be true", true);
        }

        /**
         * This test should fail, since we're saying we expect a {@link NullPointerException} but the code throws an
         * {@link ArrayIndexOutOfBoundsException}.
         */
        @Test
        @Throws(NullPointerException.class)
        public void throwsArrayIndexOutOfBounds() {
            final int[] a = {};
            @SuppressWarnings("unused")
            final int i = a[1];
        }

        /**
         *
         */
        @Test
        @Throws(NullPointerException.class)
        @SuppressWarnings("null")
        public void throwsNullPointerException() {
            final String s = null;
            s.length();
        }

        /**
         * Check that the message matching works.
         */
        @Test
        @Throws(value = IllegalArgumentException.class, message = "x should be non-negative!")
        public void throwsIllegalArgumentException() {
            throw new IllegalArgumentException("x should be non-negative");
        }
    }

    /**
     * Expects a {@link NullPointerException} but doesn't throw one.
     *
     * @author Alistair A. Israel
     */
    public static final class ExpectsExceptionButNoneThrown {

        /**
         * The rule
         */
        @Rule
        // SUPPRESS CHECKSTYLE VisibilityModifier
        public final ExpectedExceptions expectedExceptions = new ExpectedExceptions();

        /**
         * Annotated, but no exception thrown.
         */
        @Test
        @Throws(NullPointerException.class)
        public void noException() {
            assertTrue("Should be true", true);
        }
    }

}

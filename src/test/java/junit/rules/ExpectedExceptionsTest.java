/**
 *
 */
package junit.rules;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
public class ExpectedExceptionsTest {

    @Test
    public void testExpectedExceptions() {
        final Result result = JUnitCore.runClasses(UsesExpectedExceptions.class);
        final int failureCount = result.getFailureCount();
        if (failureCount == 1) {
            final Failure failure = result.getFailures().get(0);
            final Throwable e = failure.getException();
            assertNotNull(e);
            assertTrue(e instanceof ArrayIndexOutOfBoundsException);
        } else {
            System.out.println("Encountered " + failureCount + " failures, expecting only 1");
            for (final Failure failure : result.getFailures()) {
                System.out.println(failure);
                final Throwable e = failure.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @author Alistair A. Israel
     */
    public static class UsesExpectedExceptions {

        @Rule
        public final ExpectedExceptions expectedExceptions = new ExpectedExceptions();

        /**
         *
         */
        @Test
        public void noException() {
            System.out.println("noException");
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

        @Test
        @Throws(NullPointerException.class)
        @SuppressWarnings("null")
        public void throwsNullPointerException() {
            final String s = null;
            s.length();
        }

        @Test
        @Throws(value = IllegalArgumentException.class, message = "x should be non-negative!")
        public void throwsIllegalArgumentException() {
            throw new IllegalArgumentException("x should be non-negative");
        }
    }

}

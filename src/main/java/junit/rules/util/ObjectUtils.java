/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created May 5, 2011
 */
package junit.rules.util;

/**
 * @author Alistair A. Israel
 */
public final class ObjectUtils {

    /**
     * Utility classes should not have a public or default constructor.
     */
    private ObjectUtils() {
        // noop
    }

    /**
     * Null safe equals.
     *
     * @param <T>
     *        a type
     * @param a
     *        the first object to test
     * @param b
     *        the second object to test
     * @return {@code true}, if
     */
    public static <T> boolean nullSafeEquals(final T a, final T b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }
}

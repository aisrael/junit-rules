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
public final class StringUtils {

    /**
     * Utility classes should not have a public or default constructor.
     */
    private StringUtils() {
        // noop
    }

    /**
     * Checks if the given string is not {@code null} or empty.
     *
     * @param s
     *        the string to check
     * @return {@code true} if the String is not {@code null} and has length > 0
     */
    public static boolean hasLength(final String s) {
        return s != null && s.length() > 0;
    }

}

/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Apr 26, 2011
 */
package junit.rules.util;

/**
 * Used only to get around the restriction that only final variables can be visible inside anonymous inner classes.
 *
 * @param <T>
 *        a type
 * @author Alistair A. Israel
 */
public class SimpleReference<T> {

    private T value;

    /**
     * @return the object referenced
     */
    public final T get() {
        return value;
    }

    /**
     * @param obj
     *        the new object to reference
     */
    public final void set(final T obj) {
        this.value = obj;
    }

    /**
     * @param <T>
     *        a type
     * @param obj
     *        the object we want to reference
     * @return a SimpleReference to the object
     */
    public static <T> SimpleReference<T> to(final T obj) {
        return new SimpleReference<T>();
    }
}

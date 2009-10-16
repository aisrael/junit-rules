/**
 * junit-interceptors: JUnit Interceptors Collection
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 15, 2009
 */
package junit.interceptors.util;

import java.lang.reflect.Field;

/**
 * <p>
 * Provides a set of utility methods (and classes) for reflection.
 * </p>
 * <p>
 * Basically, a clever way to be able to write:
 * </p>
 *
 * <pre>
 * Reflection.set(field).of(target).to(value);
 * </pre>
 *
 * @author Alistair A. Israel
 * @since 0.3.1
 */
public final class Reflection {

    /**
     * Utility classes should not have a public or default constructor.
     */
    private Reflection() {
        // noop
    }

    /**
     * @param field
     *        the Field to set
     * @return {@link FieldWrapper}
     */
    public static FieldWrapper set(final Field field) {
        return new FieldWrapper(field);
    }

    /**
     * <p>
     * Let's us write <code>ReflectionUtils.set(field)</code>.
     * </p>
     */
    public static class FieldWrapper {

        private final Field field;

        /**
         * @param field
         *        {@link Field}
         */
        public FieldWrapper(final Field field) {
            this.field = field;
        }

        /**
         * @param target
         *        the object whose field we're setting
         * @return Setter
         */
        public final Setter of(final Object target) {
            return new Setter(target);
        }

        /**
         * <p>
         * Let's us write <code>ReflectionUtils.set(field).of(target)</code>.
         * </p>
         */
        public class Setter {

            private final Object target;

            /**
             * @param target
             *        the object to set the field value for
             */
            public Setter(final Object target) {
                this.target = target;
            }

            /**
             * <p>
             * Let's us write
             * <code>ReflectionUtils.set(field).of(target).to(value)</code> .
             * </p>
             *
             * @param value
             *        the value to set the field to
             */
            public final void to(final Object value) {
                quietlySetField(field, target, value);
            }
        }
    }

    /**
     * @param field
     *        the field to set
     * @param target
     *        the object whose field we're setting
     * @param value
     *        the value to set it to
     */
    public static void quietlySetField(final Field field, final Object target, final Object value) {
        try {
            final boolean accessible = field.isAccessible();
            if (!accessible) {
                field.setAccessible(true);
            }
            field.set(target, value);
            if (!accessible) {
                field.setAccessible(false);
            }
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

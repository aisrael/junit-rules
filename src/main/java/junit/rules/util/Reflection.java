/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 15, 2009
 */
package junit.rules.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
     * Lets us write <code>ReflectionUtils.set(field)</code>.
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
         * Lets us write <code>ReflectionUtils.set(field).of(target)</code>.
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
             * Let's us write <code>ReflectionUtils.set(field).of(target).to(value)</code> .
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

    /**
     * <p>
     * Allows us to cleverly write:
     * </p>
     *
     * <pre>
     * Reflection.invoke(method).on(object);
     * </pre>
     *
     * @author Alistair A. Israel
     * @since 0.3.1
     */
    public static class MethodInvoker {

        private final Method method;

        /**
         * @param method
         *        the method to invoke
         */
        public MethodInvoker(final Method method) {
            this.method = method;
        }

        /**
         * @param object
         *        the ojbect to invoke the method on
         * @param params
         *        any parameters to the method invocation
         * @return any return value
         */
        public final Object on(final Object object, final Object... params) {
            return quietlyInvokeMethod(method, object, params);
        }
    }

    /**
     * @param method
     *        the method to invoke
     * @return {@link MethodInvoker}
     */
    public static MethodInvoker invoke(final Method method) {
        return new MethodInvoker(method);
    }

    /**
     * @param method
     *        the method to invoke
     * @param object
     *        the object to invoke the method on
     * @param params
     *        any parameters to the method
     * @return any return value
     */
    public static Object quietlyInvokeMethod(final Method method, final Object object,
            final Object... params) {
        try {
            final boolean accessible = method.isAccessible();
            if (!accessible) {
                method.setAccessible(true);
            }
            try {
                return method.invoke(object, params);
            } finally {
                if (!accessible) {
                    method.setAccessible(false);
                }
            }
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (final InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Sep 5, 2011
 */
package junit.rules;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Alistair A. Israel
 */
@Target({ ElementType.METHOD })
@Retention(RUNTIME)
public @interface Throws {

    /**
     * The {@link Throwable} to expect
     */
    Class<? extends Throwable> value();

    /**
     * The expected message
     */
    String message() default DEFAULT;

    /**
     * A default value for the exception message, only because Java annotations can't have {@code null} as a value.
     */
    String DEFAULT = "\0";
}

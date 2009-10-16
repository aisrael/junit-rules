/**
 * junit-interceptors: JUnit Interceptors Collection
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 16, 2009
 */
package junit.interceptors.dbunit;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Alistair A. Israel
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Fixtures {

    /**
     * The fixture names.
     */
    String[] value() default { };
}

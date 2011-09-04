/**
 *
 */
package junit.rules;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Alistair A. Israel
 *
 */
@Target({ ElementType.METHOD })
@Retention(RUNTIME)
public @interface Throws {

    Class<? extends Throwable> value();

    String message() default DEFAULT;

    static final String DEFAULT = "\0";
}

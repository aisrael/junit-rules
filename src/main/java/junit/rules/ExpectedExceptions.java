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

import java.lang.reflect.Method;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Alistair A. Israel
 * @since 0.5.1
 */
public class ExpectedExceptions implements TestRule {

    /**
     * {@inheritDoc}
     *
     * @see org.junit.rules.TestRule#apply(org.junit.runners.model.Statement, org.junit.runner.Description)
     */
    @Override
    public final Statement apply(final Statement base, final Description description) {
        final Class<?> testClass = description.getTestClass();
        final String methodName = description.getMethodName();
        try {
            final Method method = testClass.getMethod(methodName);
            if (method == null) {
                fail("getMethod(\"" + methodName + "\") returned null!");
            }
            final Throws throwsAnnotation = method.getAnnotation(Throws.class);
            if (throwsAnnotation == null) {
                return base;
            }
            final Class<? extends Throwable> expected = throwsAnnotation.value();
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    try {
                        base.evaluate();
                        fail("Expected exception " + expected.getName() + " not thrown!");
                    } catch (final Throwable t) {
                        if (!expected.isInstance(t)) {
                            throw t;
                        }
                    }
                }
            };
        } catch (final SecurityException e) {
            throw new RuntimeException(e);
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}

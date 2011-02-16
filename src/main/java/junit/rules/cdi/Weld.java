/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Feb 15, 2011
 */
package junit.rules.cdi;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

import org.jboss.weld.environment.se.WeldContainer;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a CDI (JSR-299) test environment using JBoss Weld.
 *
 * @author Alistair A. Israel
 */
public class Weld implements MethodRule {

    private static final Logger logger = LoggerFactory.getLogger(Weld.class);

    private final org.jboss.weld.environment.se.Weld weld;

    private WeldContainer weldContainer;

    /**
     *
     */
    public Weld() {
        logger.trace("Weld()");
        weld = new org.jboss.weld.environment.se.Weld();
        weldContainer = weld.initialize();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.junit.rules.MethodRule#apply(org.junit.runners.model.Statement,
     *      org.junit.runners.model.FrameworkMethod, java.lang.Object)
     */
    @Override
    public final Statement apply(final Statement base, final FrameworkMethod method,
            final Object target) {
        logger.trace("apply({}.{})", method.getMethod().getDeclaringClass().getCanonicalName(), method.getName());
        final BeanManager bm = weldContainer.getBeanManager();
        @SuppressWarnings("unchecked")
        final AnnotatedType<Object> at =
                (AnnotatedType<Object>) bm.createAnnotatedType(target.getClass());
        final InjectionTarget<Object> it = bm.createInjectionTarget(at);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                // before
                final CreationalContext<Object> cc = bm.createCreationalContext(null);
                it.inject(target, cc);

                // yield
                base.evaluate();

                // after
                weld.shutdown();
            }
        };
    }

}

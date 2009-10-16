/**
 * junit-interceptors: JUnit Interceptors Collection
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 16, 2009
 */
package junit.interceptors.dbunit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.interceptors.TestFixture;

/**
 * @author Alistair A. Israel
 */
public class DatabaseFixtures extends TestFixture {

    private final List<String> fixtureNames = new ArrayList<String>();

    /**
     * @param fixtureNames
     *        list of fixture names
     */
    public DatabaseFixtures(final String[] fixtureNames) {
        for (final String fixtureName : fixtureNames) {
            this.fixtureNames.add(fixtureName);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.interceptors.TestFixture#inspect(java.lang.Object,
     *      java.lang.reflect.Method)
     */
    @Override
    protected final void inspect(final Object target, final Method method) {
        final Class<? extends Object> targetClass = target.getClass();
        if (targetClass.isAnnotationPresent(Fixtures.class)) {
            addFixturesFromAnnotation(targetClass.getAnnotation(Fixtures.class));
        }
        if (method.isAnnotationPresent(Fixtures.class)) {
            addFixturesFromAnnotation(method.getAnnotation(Fixtures.class));
        }
    }

    /**
     * @param annotation
     *        the {@link Fixtures} annotation
     */
    private void addFixturesFromAnnotation(final Fixtures annotation) {
        for (final String fixtureName : annotation.value()) {
            fixtureNames.add(fixtureName);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.interceptors.TestFixture#setUp()
     */
    @Override
    protected void setUp() throws Throwable {
    }
}

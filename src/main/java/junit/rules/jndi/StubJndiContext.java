/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Jun 2, 2011
 */
package junit.rules.jndi;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;

import junit.rules.TestFixture;

/**
 * A 'stub' JNDI Context backed by a simple {@link Map}, useful only for unit testing. This implementation is
 * <em>not</em> thread-safe, and should <em>not</em> be used in production. Consider using Spring's SimpleNamingContext
 * for a more robust implementation with more features.
 *
 * @author Alistair.Israel
 */
public class StubJndiContext extends TestFixture {

    private static final Logger logger = Logger.getLogger(StubJndiContext.class.getCanonicalName());

    private final Map<String, Object> boundObjects = new HashMap<String, Object>();

    private boolean closed;

    /**
     * The {@link InitialContextFactoryBuilder} for our {@link StubContext}
     *
     * @author Alistair A. Israel
     */
    private class StubContextFactoryBuilder implements InitialContextFactoryBuilder {

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.spi.InitialContextFactoryBuilder#createInitialContextFactory(java.util.Hashtable)
         */
        @Override
        public InitialContextFactory createInitialContextFactory(final Hashtable<?, ?> environment)
                throws NamingException {
            return new InitialContextFactory() {
                @Override
                public Context getInitialContext(final Hashtable<?, ?> environment) throws NamingException {
                    return new StubContext(environment);
                }
            };
        }
    }

    /**
     * @param name
     *        the name to bind to
     * @param obj
     *        the object to bind
     */
    public final void bind(final String name, final Object obj) {
        boundObjects.put(name, obj);
        logger.finest("Bound " + obj.getClass().getCanonicalName() + "@" + System.identityHashCode(obj) + " to \""
                + name + "\"");
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.rules.TestFixture#setUp()
     */
    @Override
    protected final void setUp() throws Throwable {
        logger.info("Activating stub JNDI context");
        if (!NamingManager.hasInitialContextFactoryBuilder()) {
            try {
                NamingManager.setInitialContextFactoryBuilder(new StubContextFactoryBuilder());
            } catch (final NamingException e) {
                throw new RuntimeException(e.getClass().getCanonicalName()
                        + " attempting to activate MockJndiContextBuilder", e);
            }
        }
    }

    /**
     * @return if {@link StubContext#close()} was called
     */
    public final boolean wasClosed() {
        return closed;
    }

    /**
     * Our internal, stub JNDI context.
     */
    private class StubContext implements Context {

        private final Hashtable<String, Object> environment;

        /**
         * @param environment
         *        this context's environment
         */
        public StubContext(final Hashtable<?, ?> environment) {
            this.environment = new Hashtable<String, Object>();
            if (environment != null && !environment.isEmpty()) {
                for (final Entry<?, ?> entry : environment.entrySet()) {
                    this.environment.put(entry.getKey().toString(), entry.getValue());
                }
            }
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#addToEnvironment(java.lang.String, java.lang.Object)
         */
        @Override
        public Object addToEnvironment(final String propName, final Object propVal) throws NamingException {
            return environment.put(propName, propVal);
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#bind(java.lang.String, java.lang.Object)
         */
        @Override
        public void bind(final String name, final Object obj) throws NamingException {
            boundObjects.put(name, obj);
            logger.finest("Bound \"" + name + "\" to " + obj.getClass().getCanonicalName() + "@"
                    + System.identityHashCode(obj));
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#close()
         */
        @Override
        public void close() throws NamingException {
            closed = true;
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#composeName(java.lang.String, java.lang.String)
         */
        @Override
        public String composeName(final String name, final String prefix) throws NamingException {
            return prefix + name;
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#createSubcontext(java.lang.String)
         */
        @Override
        public Context createSubcontext(final String name) throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#destroySubcontext(java.lang.String)
         */
        @Override
        public void destroySubcontext(final String name) throws NamingException {
            // TODO Auto-generated method stub

        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#getEnvironment()
         */
        @Override
        public Hashtable<?, ?> getEnvironment() throws NamingException {
            return this.environment;
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#list(java.lang.String)
         */
        @Override
        public NamingEnumeration<NameClassPair> list(final String name) throws NamingException {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#listBindings(java.lang.String)
         */
        @Override
        public NamingEnumeration<Binding> listBindings(final String name) throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#lookup(java.lang.String)
         */
        @Override
        public Object lookup(final String name) throws NamingException {
            final Object o = boundObjects.get(name);
            if (o != null) {
                logger.finest("lookup(\"" + name + "\") returning " + o.getClass().getCanonicalName() + "@"
                        + System.identityHashCode(o));
            } else {
                logger.finest("lookup(\"" + name + "\") returning null");
            }
            return o;
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#lookupLink(java.lang.String)
         */
        @Override
        public Object lookupLink(final String name) throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#rebind(java.lang.String, java.lang.Object)
         */
        @Override
        public void rebind(final String name, final Object obj) throws NamingException {
            boundObjects.put(name, obj);
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#removeFromEnvironment(java.lang.String)
         */
        @Override
        public Object removeFromEnvironment(final String propName) throws NamingException {
            return environment.remove(propName);
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#rename(java.lang.String, java.lang.String)
         */
        @Override
        public void rename(final String oldName, final String newName) throws NamingException {
            if (!boundObjects.containsKey(oldName)) {
                throw new NamingException("MockJndiContext namme \"" + oldName + "\" not bound!");
            }
            if (boundObjects.containsKey(newName)) {
                throw new NamingException("MockJndiContext name \"" + oldName + "\" already bound to object of type: "
                        + boundObjects.get(newName).getClass().getCanonicalName());
            }

            final Object obj = boundObjects.remove(oldName);
            boundObjects.put(newName, obj);
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#unbind(java.lang.String)
         */
        @Override
        public void unbind(final String name) throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#bind(javax.naming.Name, java.lang.Object)
         */
        @Override
        public void bind(final Name name, final Object obj) throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#composeName(javax.naming.Name, javax.naming.Name)
         */
        @Override
        public Name composeName(final Name name, final Name prefix) throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#createSubcontext(javax.naming.Name)
         */
        @Override
        public Context createSubcontext(final Name name) throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#destroySubcontext(javax.naming.Name)
         */
        @Override
        public void destroySubcontext(final Name name) throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#getNameInNamespace()
         */
        @Override
        public String getNameInNamespace() throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#getNameParser(javax.naming.Name)
         */
        @Override
        public NameParser getNameParser(final Name name) throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#getNameParser(java.lang.String)
         */
        @Override
        public NameParser getNameParser(final String name) throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#list(javax.naming.Name)
         */
        @Override
        public NamingEnumeration<NameClassPair> list(final Name name) throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#listBindings(javax.naming.Name)
         */
        @Override
        public NamingEnumeration<Binding> listBindings(final Name name) throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#lookup(javax.naming.Name)
         */
        @Override
        public Object lookup(final Name name) throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#lookupLink(javax.naming.Name)
         */
        @Override
        public Object lookupLink(final Name name) throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#rebind(javax.naming.Name, java.lang.Object)
         */
        @Override
        public void rebind(final Name name, final Object obj) throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#rename(javax.naming.Name, javax.naming.Name)
         */
        @Override
        public void rename(final Name oldName, final Name newName) throws NamingException {
            throw notSupported();
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.naming.Context#unbind(javax.naming.Name)
         */
        @Override
        public void unbind(final Name name) throws NamingException {
            throw notSupported();
        }

    }

    /**
     * Equivalent to:
     *
     * <pre>
     * new UnsupportedOperationException(&quot;ClassName.MethodName is not supported!&quot;);
     * </pre>
     *
     * @return {@link UnsupportedOperationException}
     */
    public static UnsupportedOperationException notSupported() {
        final StackTraceElement[] unwound = unwindStackTrace(Thread.currentThread().getStackTrace());
        final UnsupportedOperationException e = new UnsupportedOperationException("MockJndiContext."
                + unwound[0].getMethodName() + " is not supported!");
        e.setStackTrace(unwound);
        return e;
    }

    /**
     * @param st
     *        the {@link StackTraceElement}[]
     * @return the remaining {@link StackTraceElement}[]
     */
    private static StackTraceElement[] unwindStackTrace(final StackTraceElement[] st) {
        final int len = st.length;

        int i = 0;
        // Unwind up to MockJndiContext
        final String stubJndiContext = StubJndiContext.class.getName();
        while (i < len && !st[i].getClassName().equals(stubJndiContext)) {
            ++i;
        }
        // Unwind past unwindStackTrace() and notSupported()
        i += 2;

        final int rest = len - i;
        final StackTraceElement[] unwound = new StackTraceElement[rest];
        System.arraycopy(st, i, unwound, 0, rest);
        return unwound;
    }
}

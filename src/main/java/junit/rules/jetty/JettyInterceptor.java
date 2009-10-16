/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Sep 3, 2009
 */
package junit.rules.jetty;

import junit.rules.TestFixture;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;

/**
 * @author Alistair A. Israel
 */
public class JettyInterceptor extends TestFixture {

    /**
     * The default HTTP port to listen to, port 80
     */
    public static final int DEFAULT_HTTP_PORT = 8000;

    private final int port;

    private Server server;

    /**
     *
     */
    public JettyInterceptor() {
        this(DEFAULT_HTTP_PORT);
    }

    /**
     * @param port
     *        the port to listen (HTTP) on
     */
    public JettyInterceptor(final int port) {
        this.port = port;
    }

    /**
     * @return the port
     */
    public final int getPort() {
        return port;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.junit.rules.ExternalResource#before()
     */
    @Override
    protected final void setUp() throws Throwable {
        super.setUp();
        server = new Server(port);
        server.start();
    }

    /**
     * @param handler
     *        {@link Handler}
     * @see org.mortbay.jetty.handler.HandlerWrapper#setHandler(org.mortbay.jetty.Handler)
     */
    public final void setHandler(final Handler handler) {
        server.setHandler(handler);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.junit.rules.ExternalResource#after()
     */
    @Override
    protected final void tearDown() throws Throwable {
        server.stop();
        super.tearDown();
    }
}

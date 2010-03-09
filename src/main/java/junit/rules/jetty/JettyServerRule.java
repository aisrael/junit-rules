/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Sep 3, 2009
 */
package junit.rules.jetty;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

import junit.rules.httpserver.BaseHttpServerRule;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;

/**
 * @author Alistair A. Israel
 */
public class JettyServerRule extends BaseHttpServerRule {

    private final int port;

    private Server server;

    /**
     *
     */
    public JettyServerRule() {
        this(DEFAULT_HTTP_PORT);
    }

    /**
     * @param port
     *        the port to listen (HTTP) on
     */
    public JettyServerRule(final int port) {
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
    }

    /**
     * @param path
     *        the URI path to GET
     * @return the HttpURLConnection
     * @throws IOException
     *         on exception
     */
    @Override
    public final HttpURLConnection get(final String path) throws IOException {
        final InetSocketAddress address = new InetSocketAddress(getPort());
        final URL url = new URL("http://" + address.getHostName() + ":" + address.getPort() + path);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection;
    }

}

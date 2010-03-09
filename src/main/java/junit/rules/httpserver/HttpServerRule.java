/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Aug 28, 2009
 */
package junit.rules.httpserver;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 *
 * @author Alistair A. Israel
 */
public class HttpServerRule extends BaseHttpServerRule {

    private final InetSocketAddress address;

    private HttpServer httpServer;

    /**
     *
     */
    public HttpServerRule() {
        this(DEFAULT_HTTP_PORT);
    }

    /**
     * @param port
     *        the port to listen (HTTP) on
     */
    public HttpServerRule(final int port) {
        this(new InetSocketAddress(port));
    }

    /**
     * @param address
     *        {@link InetSocketAddress}
     */
    public HttpServerRule(final InetSocketAddress address) {
        this.address = address;
    }

    /**
     * @return the address
     */
    public final InetSocketAddress getAddress() {
        return address;
    }

    /**
     * @param path
     *        the root URI path to associate the context with
     * @param handler
     *        the handler to invoke for incoming requests
     */
    public final void addHandler(final String path, final HttpHandler handler) {
        httpServer.createContext(path, handler);
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.rules.TestFixture#setUp()
     */
    @Override
    protected final void setUp() throws Throwable {
        httpServer = HttpServer.create(address, 0);
        httpServer.start();
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.rules.TestFixture#tearDown()
     */
    @Override
    protected final void tearDown() throws Throwable {
        httpServer.stop(0);
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
        final URL url = new URL("http://" + address.getHostName() + ":" + address.getPort() + path);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection;
    }
}

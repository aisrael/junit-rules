/**
 * junit-interceptors: JUnit Interceptors Collection
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Sep 1, 2009
 */
package junit.interceptors.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Simplifies the effort to write an {@link HttpHandler}.
 *
 * @author Alistair A. Israel
 */
public abstract class SimpleHttpHandler implements HttpHandler {

    /**
     * HTTP OK ({@value HttpURLConnection#HTTP_OK})
     */
    public static final int HTTP_OK = HttpURLConnection.HTTP_OK;

    private HttpExchange httpExchange;

    private ByteArrayOutputStream out;

    private PrintWriter pw;

    private int responseCodeSent;

    /**
     * {@inheritDoc}
     *
     * @see com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange)
     */
    @Override
    public final void handle(final HttpExchange exchange) throws IOException {
        this.httpExchange = exchange;
        this.out = new ByteArrayOutputStream();
        this.pw = new PrintWriter(out);
        responseCodeSent = -1;
        onGet();
        if (responseCodeSent == -1) {
            sendResponse(HTTP_OK);
        }
    }

    /**
     * @return the httpExchange
     */
    public final HttpExchange getHttpExchange() {
        return httpExchange;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.sun.net.httpserver.HttpExchange#getRequestURI()
     */
    protected final URI getRequestURI() {
        return httpExchange.getRequestURI();
    }

    /**
     * {@inheritDoc}
     *
     * @see com.sun.net.httpserver.HttpExchange#getRequestBody()
     */
    protected final InputStream getRequestBody() {
        return httpExchange.getRequestBody();
    }

    /**
     * {@inheritDoc}
     *
     * @see com.sun.net.httpserver.HttpExchange#getRequestHeaders()
     */
    protected final Headers getRequestHeaders() {
        return httpExchange.getRequestHeaders();
    }

    /**
     * {@inheritDoc}
     *
     * @see com.sun.net.httpserver.HttpExchange#getRequestMethod()
     */
    protected final String getRequestMethod() {
        return httpExchange.getRequestMethod();
    }

    /**
     * Returns a {@link PrintWriter} to the reponse buffer used to calculate the
     * byte length of the actual HTTP response to be sent later.
     *
     * @return a {@link PrintWriter}
     */
    protected final PrintWriter getResponse() {
        return this.pw;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.sun.net.httpserver.HttpExchange#getResponseHeaders()
     */
    protected final Headers getResponseHeaders() {
        return httpExchange.getResponseHeaders();
    }

    /**
     * @throws IOException
     *         on exception
     */
    protected abstract void onGet() throws IOException;

    /**
     * @param responseCode
     *        the HTTP response code to send
     * @throws IOException
     *         on exception
     */
    protected final void sendResponse(final int responseCode) throws IOException {
        pw.flush();
        httpExchange.sendResponseHeaders(responseCode, out.size());
        out.writeTo(httpExchange.getResponseBody());
        httpExchange.close();
        responseCodeSent = responseCode;
    }
}

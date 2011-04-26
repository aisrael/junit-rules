/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Apr 26, 2011
 */
package junit.rules.jetty;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alistair A. Israel
 */
public class SimpleJettyHandler extends AbstractHandler {

    private static final Logger logger = LoggerFactory.getLogger(SimpleJettyHandler.class);

    /**
     * {@value #GET}
     */
    private static final String GET = "GET";

    /**
     * {@value #POST}
     */
    private static final String POST = "POST";

    /**
     * {@value #PUT}
     */
    private static final String PUT = "PUT";

    /**
     * {@value #DELETE}
     */
    private static final String DELETE = "DELETE";

    private HttpServletRequest httpServletRequest;

    private HttpServletResponse httpServletResponse;

    private PrintWriter responseWriter;

    private int responseCodeSent;

    /**
     * {@inheritDoc}
     *
     * @see org.mortbay.jetty.Handler#handle(java.lang.String, javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, int)
     */
    @Override
    public final void handle(final String target, final HttpServletRequest request, final HttpServletResponse response,
            final int dispatch) throws IOException, ServletException {
        responseCodeSent = -1;
        httpServletRequest = request;
        httpServletResponse = response;
        responseWriter = response.getWriter();
        final String requestMethod = request.getMethod();
        logger.debug(requestMethod + " " + request.getRequestURI());
        if (GET.equalsIgnoreCase(requestMethod)) {
            onGet();
        } else if (POST.equalsIgnoreCase(requestMethod)) {
            onPost();
        } else if (PUT.equalsIgnoreCase(requestMethod)) {
            onPut();
        } else if (DELETE.equalsIgnoreCase(requestMethod)) {
            onDelete();
        }
        if (responseCodeSent == -1) {
            sendResponse(HttpServletResponse.SC_OK);
        }
    }

    /**
     * @return the httpServletRequest
     */
    public final HttpServletRequest request() {
        return httpServletRequest;
    }

    /**
     * @return the httpServletResponse
     */
    public final HttpServletResponse response() {
        return httpServletResponse;
    }

    /**
     * @return the responseWriter
     */
    public final PrintWriter getResponseWriter() {
        return responseWriter;
    }

    /**
     * @throws IOException
     *         on exception
     */
    protected void onGet() throws IOException {
    }

    /**
     * @throws IOException
     *         on exception
     */
    protected void onPost() throws IOException {
    }

    /**
     * @throws IOException
     *         on exception
     */
    protected void onPut() throws IOException {
    }

    /**
     * @throws IOException
     *         on exception
     */
    protected void onDelete() throws IOException {
    }

    /**
     * @param responseCode
     *        the HTTP response code to send
     * @throws IOException
     *         on exception
     */
    protected final void sendResponse(final int responseCode) throws IOException {
        responseWriter.flush();
        httpServletResponse.setStatus(responseCode);
        httpServletResponse.flushBuffer();
        responseCodeSent = responseCode;
    }

}

/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Aug 28, 2009
 */
package junit.rules.httpserver;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import org.junit.Rule;
import org.junit.Test;

/**
 * JUnit test case for {@link HttpServerRule}.
 *
 * @author Alistair A. Israel
 */
public final class HttpServerRuleTest {

    // CHECKSTYLE:OFF
    @Rule
    public final HttpServerRule httpServer = new HttpServerRule();

    // CHECKSTYLE:ON

    /**
     * Test and illustrate basic usage (HTTP GET).
     *
     * @throws Exception
     *         should never happen
     */
    @Test
    public void testHttpServerInterceptor() throws Exception {
        httpServer.addHandler("/", new SimpleHttpHandler() {

            @Override
            protected void onGet() throws IOException {
                getResponseWriter().println("<?xml version=\"1.0\"?>");
                getResponseWriter().println("<resource id=\"1234\" name=\"test\" />");
                sendResponse(HTTP_OK);
            }
        });
        final HttpURLConnection connection = httpServer.get("/");
        final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        assertEquals("<?xml version=\"1.0\"?>", in.readLine());
        assertEquals("<resource id=\"1234\" name=\"test\" />", in.readLine());
        assertEquals(HTTP_OK, connection.getResponseCode());
    }

    /**
     * Test HTTP POST
     *
     * @throws Exception
     *         should never happen
     */
    @Test
    public void testHttpServerInterceptorPostMethod() throws Exception {
        httpServer.addHandler("/", new SimpleHttpHandler() {
            @Override
            protected void onPost() throws IOException {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(this.getRequestBody()));
                this.getResponseWriter().write(reader.readLine());
                sendResponse(HTTP_OK);
            }
        });
        final HttpURLConnection connection = httpServer.post("/");
        final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        out.write("Hello World");
        out.flush();
        final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        assertEquals("Hello World", in.readLine());
        assertEquals(HTTP_OK, connection.getResponseCode());
    }

    /**
     * Test HTTP PUT
     *
     * @throws Exception
     *         should never happen
     */
    @Test
    public void testHttpServerInterceptorPutMethod() throws Exception {
        httpServer.addHandler("/", new SimpleHttpHandler() {
            @Override
            protected void onPut() throws IOException {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(this.getRequestBody()));
                this.getResponseWriter().write(reader.readLine());
                sendResponse(HTTP_OK);
            }
        });
        final HttpURLConnection connection = httpServer.put("/");
        final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        out.write("Hello Again");
        out.flush();
        final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        assertEquals("Hello Again", in.readLine());
        assertEquals(HTTP_OK, connection.getResponseCode());
    }

    /**
     * Test HTTP DELETE
     *
     * @throws Exception
     *         should never happen
     */
    @Test
    public void testHttpServerInterceptorDeleteMethod() throws Exception {
        final boolean[] deleteIssued = new boolean[] { false };
        httpServer.addHandler("/", new SimpleHttpHandler() {
            @Override
            protected void onDelete() throws IOException {
                deleteIssued[0] = true;
                sendResponse(HTTP_OK);
            }
        });
        final HttpURLConnection connection = httpServer.delete("/");
        assertEquals(HTTP_OK, connection.getResponseCode());
        assertTrue(deleteIssued[0]);
    }
}

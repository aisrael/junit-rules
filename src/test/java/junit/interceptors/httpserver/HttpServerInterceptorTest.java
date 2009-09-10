/**
 * junit-interceptors: JUnit Interceptors Collection
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Aug 28, 2009
 */
package junit.interceptors.httpserver;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.junit.Rule;
import org.junit.Test;

/**
 * JUnit test case for {@link HttpServerInterceptor}.
 *
 * @author Alistair A. Israel
 */
public final class HttpServerInterceptorTest {

    // CHECKSTYLE:OFF
    @Rule
    public final HttpServerInterceptor httpServer = new HttpServerInterceptor();
    // CHECKSTYLE:ON

    /**
     * @throws Exception
     *         on exception
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
        final BufferedReader in =
                new BufferedReader(new InputStreamReader(connection.getInputStream()));
        assertEquals("<?xml version=\"1.0\"?>", in.readLine());
        assertEquals("<resource id=\"1234\" name=\"test\" />", in.readLine());
        assertEquals(HTTP_OK, connection.getResponseCode());
    }

}

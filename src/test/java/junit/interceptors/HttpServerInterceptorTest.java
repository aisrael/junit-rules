/**
 * junit-interceptors: JUnit Interceptors Collection
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Aug 28, 2009
 */
package junit.interceptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.HttpURLConnection;

import junit.interceptors.httpserver.SimpleHttpHandler;

import org.junit.Rule;
import org.junit.Test;

import com.sun.net.httpserver.HttpHandler;

/**
 * JUnit test case for {@link HttpServerInterceptor}.
 *
 * @author Alistair A. Israel
 */
public final class HttpServerInterceptorTest {

    // CHECKSTYLE:OFF
    @Rule
    public final HttpServerInterceptor httpServer = new HttpServerInterceptor(8080);
    // CHECKSTYLE:ON

    private boolean gotHttpRequest;

    /**
     * @throws Exception
     *         on exception
     */
    @Test
    public void testHttpServerInterceptor() throws Exception {
        final HttpHandler handler = new SimpleHttpHandler() {

            @Override
            protected void onGet() throws IOException {
                gotHttpRequest = true;
            }
        };
        httpServer.addHandler("/", handler);
        final HttpURLConnection connection = httpServer.get("/");
        assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
        assertTrue(gotHttpRequest);
    }

}

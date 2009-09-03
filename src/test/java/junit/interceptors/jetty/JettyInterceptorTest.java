/**
 * junit-interceptors: JUnit Interceptors Collection
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Aug 28, 2009
 */
package junit.interceptors.jetty;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Rule;
import org.junit.Test;
import org.mortbay.jetty.handler.AbstractHandler;

/**
 * JUnit test case for {@link JettyInterceptor}
 *
 * @author Alistair A. Israel
 */
public final class JettyInterceptorTest {

    // CHECKSTYLE:OFF
    @Rule
    public final JettyInterceptor jetty = new JettyInterceptor();

    // CHECKSTYLE:ON

    /**
     * @throws Exception
     *         on exception
     */
    @Test
    public void testHttpServerInterceptor() throws Exception {
        jetty.setHandler(new AbstractHandler() {

            @Override
            public void handle(final String target, final HttpServletRequest request,
                    final HttpServletResponse response, final int dispatch) throws IOException,
                    ServletException {
                final PrintWriter out = response.getWriter();
                out.println("<?xml version=\"1.0\"?>");
                out.println("<resource id=\"1234\" name=\"test\" />");
                response.setStatus(HttpServletResponse.SC_OK);
                response.flushBuffer();
            }
        });

        final URL url = new URL("http://localhost:" + jetty.getPort() + "/1234.xml");
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        assertEquals("<?xml version=\"1.0\"?>", in.readLine());
        assertEquals("<resource id=\"1234\" name=\"test\" />", in.readLine());
        assertEquals(HTTP_OK, connection.getResponseCode());
    }
}

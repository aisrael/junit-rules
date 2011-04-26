/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Aug 28, 2009
 */
package junit.rules.jetty;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletResponse;

import junit.rules.util.SimpleReference;

import org.junit.Rule;
import org.junit.Test;

/**
 * <p>
 * JUnit test case for {@link JettyServerRule}.
 * </p>
 * <p>
 * With contributions by <a href="https://github.com/beobal">Sam Tunnicliffe</a>.
 * </p>
 *
 * @author Alistair A. Israel
 */
public final class JettyServerRuleTest {

    // CHECKSTYLE:OFF
    @Rule
    public final JettyServerRule jettyServer = new JettyServerRule();

    // CHECKSTYLE:ON

    /**
     * @throws Exception
     *         on exception
     */
    @Test
    public void testJettyServerRule() throws Exception {
        jettyServer.setHandler(new SimpleJettyHandler() {
            @Override
            protected void onGet() throws IOException {
                final PrintWriter out = getResponseWriter();
                out.println("<?xml version=\"1.0\"?>");
                out.println("<resource id=\"1234\" name=\"test\" />");
                sendResponse(HttpServletResponse.SC_OK);
            }
        });

        final HttpURLConnection connection = jettyServer.get("/1234.xml");
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
    public void testJettyServerRulePostMethod() throws Exception {
        jettyServer.setHandler(new SimpleJettyHandler() {
            @Override
            protected void onPost() throws IOException {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(request().getInputStream()));
                final PrintWriter out = getResponseWriter();
                out.println(reader.readLine());
                sendResponse(HttpServletResponse.SC_OK);
            }
        });
        final HttpURLConnection connection = jettyServer.post("/");
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
    public void testJettyServerRulePutMethod() throws Exception {
        jettyServer.setHandler(new SimpleJettyHandler() {
            @Override
            protected void onPut() throws IOException {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(request().getInputStream()));
                final PrintWriter out = getResponseWriter();
                out.println(reader.readLine());
                sendResponse(HttpServletResponse.SC_OK);
            }
        });
        final HttpURLConnection connection = jettyServer.put("/");
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
    public void testJettyServerRuleDeleteMethod() throws Exception {
        final SimpleReference<Boolean> deleteIssued = SimpleReference.to(FALSE);
        jettyServer.setHandler(new SimpleJettyHandler() {
            @Override
            protected void onDelete() throws IOException {
                deleteIssued.set(TRUE);
                sendResponse(HttpServletResponse.SC_OK);
            }
        });
        final HttpURLConnection connection = jettyServer.delete("/");
        assertEquals(HTTP_OK, connection.getResponseCode());
        assertTrue(deleteIssued.get());
    }
}

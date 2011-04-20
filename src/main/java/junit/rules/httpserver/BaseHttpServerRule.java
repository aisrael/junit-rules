/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Mar 9, 2010
 */
package junit.rules.httpserver;

import java.io.IOException;
import java.net.HttpURLConnection;

import junit.rules.TestFixture;

/**
 * @author Alistair A. Israel
 */
public abstract class BaseHttpServerRule extends TestFixture {

    /**
     * The default HTTP port to listen to, port 8000
     */
    public static final int DEFAULT_HTTP_PORT = 8000;

    /**
     * @param path
     *        the URI path to GET
     * @return the HttpURLConnection
     * @throws IOException
     *         on exception
     */
    public abstract HttpURLConnection get(final String path) throws IOException;

    /**
     * @param path
     *        the URI path to POST to
     * @return the HttpURLConnection
     * @throws IOException
     *         on exception
     */
    public abstract HttpURLConnection post(final String path) throws IOException;

    /**
     * @param path
     *        the URI path to PUT to
     * @return the HttpURLConnection
     * @throws IOException
     *         on exception
     */
    public abstract HttpURLConnection put(final String path) throws IOException;

    /**
     * @param path
     *        the URI path to DELETE
     * @return the HttpURLConnection
     * @throws IOException
     *         on exception
     */
    public abstract HttpURLConnection delete(final String path) throws IOException;
}

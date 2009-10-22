/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 22, 2009
 */
package junit.rules;

import junit.rules.httpserver.HttpServerInterceptorTest;
import junit.rules.jetty.JettyInterceptorTest;
import junit.rules.jpa.hibernate.DerbyHibernateTestCaseTest;
import junit.rules.jpa.hibernate.HibernatePersistenceContextTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Alistair A. Israel
 */
@RunWith(Suite.class)
@SuiteClasses({
    HttpServerInterceptorTest.class,
    JettyInterceptorTest.class,
    DerbyHibernateTestCaseTest.class,
    HibernatePersistenceContextTest.class }
)
public class AllTests {

}

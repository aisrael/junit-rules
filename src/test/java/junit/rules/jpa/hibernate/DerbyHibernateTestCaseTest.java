/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 15, 2009
 */
package junit.rules.jpa.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import junit.rules.dbunit.Fixtures;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import com.example.ejb3.beans.WidgetBean;
import com.example.model.Widget;

/**
 * @author Alistair A. Israel
 * @since 0.3
 */
public final class DerbyHibernateTestCaseTest {

    /**
     * @author Alistair A. Israel
     */
    @Fixtures("fixtures.xml")
    public static final class FirstTest extends DerbyHibernateTestCase {

        private WidgetBean widgetBean = new WidgetBean();

        /**
         *
         */
        @Before
        public void setUp() {
            injectAndPostConstruct(widgetBean);
        }

        /**
         * @throws Exception
         *         on exception
         */
        @Test
        public void testListAll() throws Exception {
            final List<Widget> widgets = widgetBean.listAll();
            assertNotNull(widgets);
            assertEquals(2, widgets.size());
        }

        /**
         * @throws Exception
         *         on exception
         */
        @Test
        public void testFindById() throws Exception {
            final Widget widget = widgetBean.findById(2);
            assertNotNull(widget);
            assertEquals(2, widget.getId().intValue());
        }
    }

    /**
     *
     */
    @Test
    public void testRunFirst() {
        JUnitCore.runClasses(FirstTest.class);
    }

    /**
     * @author Alistair A. Israel
     */
    @Fixtures("fixtures.xml")
    public static final class SecondTest extends DerbyHibernateTestCase {

        private WidgetBean widgetBean = new WidgetBean();

        /**
         * @throws Exception
         *         on exception
         */
        @Test
        public void testListAll() throws Exception {
            injectAndPostConstruct(widgetBean);
            final List<Widget> widgets = widgetBean.listAll();
            assertNotNull(widgets);
            assertEquals(2, widgets.size());
        }
    }

    /**
     *
     */
    @Test
    public void testRunFirstAndSecond() {
        JUnitCore.runClasses(FirstTest.class, SecondTest.class);
    }

}

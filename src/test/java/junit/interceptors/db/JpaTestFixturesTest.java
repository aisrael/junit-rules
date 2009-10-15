/**
 * junit-interceptors: JUnit Interceptors Collection
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 15, 2009
 */
package junit.interceptors.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.example.dao.jpa.WidgetJpaDao;
import com.example.model.Widget;

/**
 * @author Alistair A. Israel
 */
public final class JpaTestFixturesTest {

    private static EntityManagerFactory emf;

    @BeforeClass
    public static void beforeClass() {
        emf = Persistence.createEntityManagerFactory("test");
    }

    private WidgetJpaDao widgetDao;

    @Before
    public void setUp() {
        widgetDao = new WidgetJpaDao();
        widgetDao.setEntityManager(emf.createEntityManager());
    }

    /**
     *
     */
    @Test
    public void testListAll() {
        final List<Widget> widgets = widgetDao.listAll();
        assertNotNull(widgets);
        assertEquals(0, widgets.size());
    }
}

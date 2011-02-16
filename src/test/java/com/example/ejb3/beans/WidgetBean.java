/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 16, 2009
 */
package com.example.ejb3.beans;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.dao.WidgetDao;
import com.example.dao.jpa.JpaWidgetDao;
import com.example.model.Widget;

/**
 * @author Alistair A. Israel
 */
public class WidgetBean {

    @PersistenceContext
    private EntityManager em;

    private WidgetDao widgetDao;

    /**
     * We create the {@link JpaWidgetDao}
     */
    @PostConstruct
    @SuppressWarnings("unused")
    private void initialize() {
        widgetDao = new JpaWidgetDao(em);
    }

    /**
     * @return list of all widgets
     */
    public final List<Widget> listAll() {
        return widgetDao.listAll();
    }

    /**
     * @param id
     *        the Widget id
     * @return the found Widget
     */
    public final Widget findById(final int id) {
        return widgetDao.findById(id);
    }
}

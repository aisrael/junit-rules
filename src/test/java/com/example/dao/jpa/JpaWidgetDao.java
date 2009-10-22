/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 22, 2009
 */
package com.example.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import com.example.dao.WidgetDao;
import com.example.model.Widget;

/**
 * @author Alistair A. Israel
 */
public class JpaWidgetDao implements WidgetDao {

    private final EntityManager entityManager;

    /**
     * @param entityManager
     *        the {@link EntityManager}
     */
    public JpaWidgetDao(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.example.dao.WidgetDao#findById(int)
     */
    @Override
    public final Widget findById(final int id) {
        return entityManager.find(Widget.class, id);
    }

    /**
     * {@inheritDoc}
     *
     * @see com.example.dao.WidgetDao#findByName(java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public final Widget findByName(final String name) {
        final List<Widget> list = entityManager.createNamedQuery(Widget.FIND_BY_NAME).setParameter("name", name)
                .getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * {@inheritDoc}
     *
     * @see com.example.dao.WidgetDao#listAll()
     */
    @Override
    @SuppressWarnings("unchecked")
    public final List<Widget> listAll() {
        return entityManager.createNamedQuery(Widget.LIST_ALL).getResultList();
    }

}

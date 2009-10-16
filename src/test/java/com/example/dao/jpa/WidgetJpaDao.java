/**
 * junit-interceptors: JUnit Interceptors Collection
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 15, 2009
 */
package com.example.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.example.dao.WidgetDao;
import com.example.model.Widget;

/**
 * @author Alistair A. Israel
 */
public class WidgetJpaDao implements WidgetDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @return the entityManager
     */
    public final EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * @param entityManager
     *        the entityManager to set
     */
    public final void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.example.dao.WidgetDao#listAll()
     */
    @Override
    @SuppressWarnings("unchecked")
    public final List<Widget> listAll() {
        final Query query = getEntityManager().createNamedQuery(Widget.LIST_ALL);
        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     *
     * @see com.example.dao.WidgetDao#findById(int)
     */
    @Override
    @SuppressWarnings("unchecked")
    public final Widget findById(final int id) {
        final Query query = getEntityManager().createNamedQuery(Widget.FIND_BY_ID);
        query.setParameter("id", Integer.valueOf(id));
        final List results = query.getResultList();
        if (results.size() == 1) {
            return (Widget) results.get(0);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.example.dao.WidgetDao#findByName(java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public final Widget findByName(final String name) {
        final Query query = getEntityManager().createNamedQuery(Widget.FIND_BY_NAME);
        query.setParameter("name", name);
        final List results = query.getResultList();
        if (results.size() == 1) {
            return (Widget) results.get(0);
        }
        return null;
    }

}

/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 15, 2009
 */
package com.example.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * @author Alistair A. Israel
 */
@Entity
@NamedQueries({
  @NamedQuery(name = Widget.LIST_ALL, query = "SELECT w FROM Widget w"),
  @NamedQuery(name = Widget.FIND_BY_ID, query = "SELECT w FROM Widget w WHERE w.id = :id"),
  @NamedQuery(name = Widget.FIND_BY_NAME, query = "SELECT w FROM Widget w WHERE w.name = :name")
})
public class Widget implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1689099836716604893L;

    /**
     * {@value #LIST_ALL}
     */
    public static final String LIST_ALL = "Widget.listAll";

    /**
     * {@value #FIND_BY_ID}
     */
    public static final String FIND_BY_ID = "Widget.findById";

    /**
     * {@value #FIND_BY_NAME}
     */
    public static final String FIND_BY_NAME = "Widget.findByName";

    @Id
    private Integer id;

    @Column
    private String name;

    /**
     * @return the id
     */
    public final Integer getId() {
        return id;
    }

    /**
     * @param id
     *        the id to set
     */
    public final void setId(final Integer id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @param name
     *        the name to set
     */
    public final void setName(final String name) {
        this.name = name;
    }

}

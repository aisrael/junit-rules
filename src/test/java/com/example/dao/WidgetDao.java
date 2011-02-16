/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 15, 2009
 */
package com.example.dao;

import java.util.List;

import com.example.model.Widget;

/**
 * @author Alistair A. Israel
 */
public interface WidgetDao {

    /**
     * @return {@link List}&lt;{@link Widget}&gt;
     */
    List<Widget> listAll();

    /**
     * @param id
     *        the widget id
     * @return {@link Widget}
     */
    Widget findById(int id);

    /**
     * @param name
     *        the widget name
     * @return {@link Widget}
     */
    Widget findByName(String name);

}

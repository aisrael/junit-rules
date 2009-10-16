/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Oct 16, 2009
 */
package junit.rules.jpa;

/**
 * @author Alistair A. Israel
 */
public interface PersistenceContext {

    /**
     * @param object
     *        an object to which we will apply EJB 3.0 style @PersistenceContext and @PostConstruct handling
     */
    void injectAndPostConstruct(Object object);

}

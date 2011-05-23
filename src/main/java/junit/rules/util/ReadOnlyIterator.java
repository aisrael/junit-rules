/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created May 17, 2011
 */
package junit.rules.util;

import java.util.Iterator;

/**
 * A read-only iterator that throws {@link UnsupportedOperationException} on {@link #remove()}.
 *
 * @param <T>
 *        a type
 * @author Alistair A. Israel
 */
public abstract class ReadOnlyIterator<T> implements Iterator<T> {

    /**
     * Will throw {@link UnsupportedOperationException}.
     *
     * @see java.util.Iterator#remove()
     */
    @Override
    public final void remove() {
        throw new UnsupportedOperationException("remove() not allowed for " + this.getClass().getCanonicalName()
                + " [ReadOnlyIterator]!");
    }

}

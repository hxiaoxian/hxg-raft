package com.hxg.sofa.jraft.rhea.client;

import java.util.NoSuchElementException;

/**
 * An iterator over RheaKVStore.
 *
 * @param <E> the type of elements returned by this iterator
 *
 */
public interface RheaIterator<E> {

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    boolean hasNext();

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    E next();
}

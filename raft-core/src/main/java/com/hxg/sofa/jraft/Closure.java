package com.hxg.sofa.jraft;

/**
 * Callback closure.
 *
 *     
 *
 * 2018-Apr-03 11:07:05 AM
 */
public interface Closure {

    /**
     * Called when task is done.
     *
     * @param status the task status.
     */
    void run(final Status status);
}

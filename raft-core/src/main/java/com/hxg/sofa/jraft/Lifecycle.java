package com.hxg.sofa.jraft;


public interface Lifecycle<T> {

    boolean init(final T opts);

    void shutdown();
}

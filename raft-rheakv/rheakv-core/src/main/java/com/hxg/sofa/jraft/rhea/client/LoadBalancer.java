package com.hxg.sofa.jraft.rhea.client;

import java.util.List;

  
public interface LoadBalancer {

    /**
     * Select one from the element list.
     */
    <T> T select(final List<T> elements);
}

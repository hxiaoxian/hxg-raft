package com.hxg.sofa.jraft.rhea.client;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import com.hxg.sofa.jraft.rhea.util.Maps;

/**
 *
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    private static final ConcurrentMap<Long, RoundRobinLoadBalancer>       container    = Maps.newConcurrentMapLong();

    private static final AtomicIntegerFieldUpdater<RoundRobinLoadBalancer> indexUpdater = AtomicIntegerFieldUpdater
                                                                                            .newUpdater(
                                                                                                RoundRobinLoadBalancer.class,
                                                                                                "index");

    @SuppressWarnings("unused")
    private volatile int                                                   index        = 0;

    public static RoundRobinLoadBalancer getInstance(final long regionId) {
        RoundRobinLoadBalancer instance = container.get(regionId);
        if (instance == null) {
            RoundRobinLoadBalancer newInstance = new RoundRobinLoadBalancer();
            instance = container.putIfAbsent(regionId, newInstance);
            if (instance == null) {
                instance = newInstance;
            }
        }
        return instance;
    }

    @Override
    public <T> T select(final List<T> elements) {
        if (elements == null) {
            throw new NullPointerException("elements");
        }

        final int size = elements.size();

        if (size == 1) {
            return elements.get(0);
        }

        final int roundRobinIndex = indexUpdater.getAndIncrement(this) & Integer.MAX_VALUE;

        return elements.get(roundRobinIndex % size);
    }
}

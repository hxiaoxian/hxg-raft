package com.hxg.sofa.jraft.rhea.util;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.hxg.sofa.jraft.rhea.util.concurrent.collection.NonBlockingHashMap;
import com.hxg.sofa.jraft.rhea.util.concurrent.collection.NonBlockingHashMapLong;
import com.hxg.sofa.jraft.util.Ints;
import com.hxg.sofa.jraft.util.Requires;
import com.hxg.sofa.jraft.util.SystemPropertyUtil;
import com.hxg.sofa.jraft.util.internal.UnsafeUtil;


public final class Maps {

    private static final boolean USE_NON_BLOCKING_HASH = SystemPropertyUtil.getBoolean("rhea.use.non_blocking_hash",
                                                           true);
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
        return new HashMap<>(capacity(expectedSize));
    }

    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
        return new IdentityHashMap<>();
    }

    public static <K, V> IdentityHashMap<K, V> newIdentityHashMapWithExpectedSize(int expectedSize) {
        return new IdentityHashMap<>(capacity(expectedSize));
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<>();
    }

    /**
     * Creates a mutable, empty {@code TreeMap} instance using the natural ordering of its elements.
     */
    public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<>();
    }

    /**
     * Creates a mutable, empty {@code ConcurrentMap} instance.
     */
    public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
        if (USE_NON_BLOCKING_HASH && UnsafeUtil.hasUnsafe()) {
            return new NonBlockingHashMap<>();
        }
        return new ConcurrentHashMap<>();
    }

    /**
     * Creates a {@code ConcurrentMap} instance, with a high enough "initial capacity"
     * that it should hold {@code expectedSize} elements without growth.
     */
    public static <K, V> ConcurrentMap<K, V> newConcurrentMap(int initialCapacity) {
        if (USE_NON_BLOCKING_HASH && UnsafeUtil.hasUnsafe()) {
            return new NonBlockingHashMap<>(initialCapacity);
        }
        return new ConcurrentHashMap<>(initialCapacity);
    }

    /**
     * Creates a mutable, empty {@code NonBlockingHashMapLong} instance.
     */
    public static <V> ConcurrentMap<Long, V> newConcurrentMapLong() {
        if (USE_NON_BLOCKING_HASH && UnsafeUtil.hasUnsafe()) {
            return new NonBlockingHashMapLong<>();
        }
        return new ConcurrentHashMap<>();
    }

    /**
     * Creates a {@code NonBlockingHashMapLong} instance, with a high enough "initial capacity"
     * that it should hold {@code expectedSize} elements without growth.
     */
    public static <V> ConcurrentMap<Long, V> newConcurrentMapLong(int initialCapacity) {
        if (USE_NON_BLOCKING_HASH) {
            return new NonBlockingHashMapLong<>(initialCapacity);
        }
        return new ConcurrentHashMap<>(initialCapacity);
    }

    /**
     * Returns a capacity that is sufficient to keep the map from being resized as
     * long as it grows no larger than expectedSize and the load factor is >= its
     * default (0.75).
     */
    private static int capacity(int expectedSize) {
        if (expectedSize < 3) {
            Requires.requireTrue(expectedSize >= 0, "expectedSize cannot be negative but was: " + expectedSize);
            return expectedSize + 1;
        }
        if (expectedSize < Ints.MAX_POWER_OF_TWO) {
            return expectedSize + expectedSize / 3;
        }
        return Integer.MAX_VALUE; // any large value
    }

    private Maps() {
    }
}

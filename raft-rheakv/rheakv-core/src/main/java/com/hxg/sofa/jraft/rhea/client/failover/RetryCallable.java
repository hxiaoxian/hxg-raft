package com.hxg.sofa.jraft.rhea.client.failover;

import com.hxg.sofa.jraft.rhea.client.FutureGroup;

/**
 * A retry task that returns a {@link FutureGroup<T>} result.
 *
 */
public interface RetryCallable<T> {

    FutureGroup<T> run(final Throwable retryCause);
}
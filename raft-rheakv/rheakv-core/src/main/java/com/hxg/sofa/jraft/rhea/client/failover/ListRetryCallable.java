package com.hxg.sofa.jraft.rhea.client.failover;

import java.util.List;

import com.hxg.sofa.jraft.rhea.client.FutureGroup;

/**
 * A retry task that returns a {@link FutureGroup<List>} result.
 *
 */
public interface ListRetryCallable<T> {

    FutureGroup<List<T>> run(final Throwable retryCause);
}
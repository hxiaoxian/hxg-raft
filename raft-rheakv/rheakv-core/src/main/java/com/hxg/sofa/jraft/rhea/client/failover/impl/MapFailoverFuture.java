package com.hxg.sofa.jraft.rhea.client.failover.impl;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.hxg.sofa.jraft.rhea.client.failover.RetryCallable;
import com.hxg.sofa.jraft.rhea.errors.ApiExceptionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxg.sofa.jraft.rhea.client.FutureGroup;
import com.hxg.sofa.jraft.rhea.util.Attachable;
import com.hxg.sofa.jraft.rhea.util.Maps;
import com.hxg.sofa.jraft.rhea.util.StackTraceUtil;

/**
 * A helper object for map result failover, which is an immutable object.
 * A new object will be created when a retry operation occurs and
 * {@code retriesLeft} will decrease by 1, until {@code retriesLeft} == 0.
 *
 */
public final class MapFailoverFuture<K, V> extends CompletableFuture<Map<K, V>> implements Attachable<Object> {

    private static final Logger            LOG = LoggerFactory.getLogger(MapFailoverFuture.class);

    private final int                      retriesLeft;
    private final RetryCallable<Map<K, V>> retryCallable;
    private final Object                   attachments;

    public MapFailoverFuture(int retriesLeft, RetryCallable<Map<K, V>> retryCallable) {
        this(retriesLeft, retryCallable, null);
    }

    public MapFailoverFuture(int retriesLeft, RetryCallable<Map<K, V>> retryCallable, Object attachments) {
        this.retriesLeft = retriesLeft;
        this.retryCallable = retryCallable;
        this.attachments = attachments;
    }

    @Override
    public boolean completeExceptionally(final Throwable ex) {
        if (this.retriesLeft > 0 && ApiExceptionHelper.isInvalidEpoch(ex)) {
            LOG.warn("[InvalidEpoch-Failover] cause: {}, [{}] retries left.", StackTraceUtil.stackTrace(ex),
                    this.retriesLeft);
            final FutureGroup<Map<K, V>> futureGroup = this.retryCallable.run(ex);
            CompletableFuture.allOf(futureGroup.toArray()).whenComplete((ignored, throwable) -> {
                if (throwable == null) {
                    final Map<K, V> all = Maps.newHashMap();
                    for (final CompletableFuture<Map<K, V>> partOf : futureGroup.futures()) {
                        all.putAll(partOf.join());
                    }
                    super.complete(all);
                } else {
                    super.completeExceptionally(throwable);
                }
            });
            return false;
        }
        if (this.retriesLeft <= 0) {
            LOG.error("[InvalidEpoch-Failover] cause: {}, {} retries left.", StackTraceUtil.stackTrace(ex),
                    this.retriesLeft);
        }
        return super.completeExceptionally(ex);
    }

    @Override
    public Object getAttachments() {
        return attachments;
    }
}

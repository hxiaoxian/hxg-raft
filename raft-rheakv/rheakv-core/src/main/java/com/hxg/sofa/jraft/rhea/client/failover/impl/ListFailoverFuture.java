package com.hxg.sofa.jraft.rhea.client.failover.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.hxg.sofa.jraft.rhea.errors.ApiExceptionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxg.sofa.jraft.rhea.client.FutureGroup;
import com.hxg.sofa.jraft.rhea.client.failover.ListRetryCallable;
import com.hxg.sofa.jraft.rhea.util.Attachable;
import com.hxg.sofa.jraft.rhea.util.Lists;
import com.hxg.sofa.jraft.rhea.util.StackTraceUtil;

/**
 * A helper future for list result failover, which is an immutable object.
 * A new object will be created when a retry operation occurs and
 * {@code retriesLeft} will decrease by 1, until {@code retriesLeft} == 0.
 *
 */
public final class ListFailoverFuture<T> extends CompletableFuture<List<T>> implements Attachable<Object> {

    private static final Logger        LOG = LoggerFactory.getLogger(ListFailoverFuture.class);

    private final int                  retriesLeft;
    private final ListRetryCallable<T> retryCallable;
    private final Object               attachments;

    public ListFailoverFuture(int retriesLeft, ListRetryCallable<T> retryCallable) {
        this(retriesLeft, retryCallable, null);
    }

    public ListFailoverFuture(int retriesLeft, ListRetryCallable<T> retryCallable, Object attachments) {
        this.retriesLeft = retriesLeft;
        this.retryCallable = retryCallable;
        this.attachments = attachments;
    }

    @Override
    public boolean completeExceptionally(final Throwable ex) {
        if (this.retriesLeft > 0 && ApiExceptionHelper.isInvalidEpoch(ex)) {
            LOG.warn("[InvalidEpoch-Failover] cause: {}, [{}] retries left.", StackTraceUtil.stackTrace(ex),
                    this.retriesLeft);
            final FutureGroup<List<T>> futureGroup = this.retryCallable.run(ex);
            CompletableFuture.allOf(futureGroup.toArray()).whenComplete((ignored, throwable) -> {
                if (throwable == null) {
                    final List<T> all = Lists.newArrayList();
                    for (final CompletableFuture<List<T>> partOf : futureGroup.futures()) {
                        all.addAll(partOf.join());
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

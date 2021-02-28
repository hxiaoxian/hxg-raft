package com.hxg.sofa.jraft.rhea.client.failover.impl;

import java.util.concurrent.CompletableFuture;

import com.hxg.sofa.jraft.rhea.errors.Errors;
import com.hxg.sofa.jraft.rhea.errors.ErrorsHelper;
import com.hxg.sofa.jraft.rhea.storage.BaseKVStoreClosure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxg.sofa.jraft.Status;
import com.hxg.sofa.jraft.rhea.client.failover.FailoverClosure;
import com.hxg.sofa.jraft.rhea.client.failover.RetryRunner;

/**
 * A helper closure for failover, which is an immutable object.
 * A new object will be created when a retry operation occurs
 * and {@code retriesLeft} will decrease by 1, until
 * {@code retriesLeft} == 0.
 *
 */
public final class FailoverClosureImpl<T> extends BaseKVStoreClosure implements FailoverClosure<T> {

    private static final Logger        LOG = LoggerFactory.getLogger(FailoverClosureImpl.class);

    private final CompletableFuture<T> future;
    private final boolean              retryOnInvalidEpoch;
    private final int                  retriesLeft;
    private final RetryRunner          retryRunner;

    public FailoverClosureImpl(CompletableFuture<T> future, int retriesLeft, RetryRunner retryRunner) {
        this(future, true, retriesLeft, retryRunner);
    }

    public FailoverClosureImpl(CompletableFuture<T> future, boolean retryOnInvalidEpoch, int retriesLeft,
                               RetryRunner retryRunner) {
        this.future = future;
        this.retryOnInvalidEpoch = retryOnInvalidEpoch;
        this.retriesLeft = retriesLeft;
        this.retryRunner = retryRunner;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run(final Status status) {
        if (status.isOk()) {
            success((T) getData());
            return;
        }

        final Errors error = getError();
        if (this.retriesLeft > 0
            && (ErrorsHelper.isInvalidPeer(error) || (this.retryOnInvalidEpoch && ErrorsHelper.isInvalidEpoch(error)))) {
            LOG.warn("[Failover] status: {}, error: {}, [{}] retries left.", status, error, this.retriesLeft);
            this.retryRunner.run(error);
        } else {
            if (this.retriesLeft <= 0) {
                LOG.error("[InvalidEpoch-Failover] status: {}, error: {}, {} retries left.", status, error,
                    this.retriesLeft);
            }
            failure(error);
        }
    }

    @Override
    public CompletableFuture<T> future() {
        return future;
    }

    @Override
    public void success(final T result) {
        this.future.complete(result);
    }

    @Override
    public void failure(final Throwable cause) {
        this.future.completeExceptionally(cause);
    }

    @Override
    public void failure(final Errors error) {
        if (error == null) {
            failure(new NullPointerException(
                "The error message is missing, this should not happen, now only the stack information can be referenced."));
        } else {
            failure(error.exception());
        }
    }
}

package com.hxg.sofa.jraft.rhea.client.failover;

import java.util.concurrent.CompletableFuture;

import com.hxg.sofa.jraft.rhea.errors.Errors;
import com.hxg.sofa.jraft.rhea.storage.KVStoreClosure;

/**
 *
 */
public interface FailoverClosure<T> extends KVStoreClosure {

    CompletableFuture<T> future();

    void success(final T result);

    void failure(final Throwable cause);

    void failure(final Errors error);
}

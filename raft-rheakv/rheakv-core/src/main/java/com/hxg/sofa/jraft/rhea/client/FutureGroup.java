package com.hxg.sofa.jraft.rhea.client;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.hxg.sofa.jraft.util.Requires;

/**
 *
 */
public class FutureGroup<V> extends CompletableFuture<V> {

    private final List<CompletableFuture<V>> futures;

    private volatile CompletableFuture<V>[]  array;

    public FutureGroup(List<CompletableFuture<V>> futures) {
        this.futures = Requires.requireNonNull(futures, "futures");
    }

    public List<CompletableFuture<V>> futures() {
        return futures;
    }

    @SuppressWarnings("unchecked")
    public CompletableFuture<V>[] toArray() {
        if (this.array == null) {
            synchronized (this) {
                if (this.array == null) {
                    final CompletableFuture<V>[] temp = new CompletableFuture[this.futures.size()];
                    this.futures.toArray(temp);
                    this.array = temp;
                }
            }
        }
        return this.array;
    }

    public int size() {
        return this.futures.size();
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        boolean result = true;
        for (final CompletableFuture<V> f : this.futures) {
            result = result && f.cancel(mayInterruptIfRunning);
        }
        return result;
    }

    @Override
    public boolean isCancelled() {
        for (final CompletableFuture<V> f : this.futures) {
            if (!f.isCancelled()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isDone() {
        for (final CompletableFuture<V> f : this.futures) {
            if (!f.isDone()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("get");
    }

    @Override
    public V get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException,
                                                         TimeoutException {
        throw new UnsupportedOperationException("get");
    }
}

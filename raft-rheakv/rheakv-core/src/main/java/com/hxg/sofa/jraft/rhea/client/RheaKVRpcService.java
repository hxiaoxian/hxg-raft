package com.hxg.sofa.jraft.rhea.client;

import java.util.concurrent.CompletableFuture;

import com.hxg.sofa.jraft.Lifecycle;
import com.hxg.sofa.jraft.rhea.client.failover.FailoverClosure;
import com.hxg.sofa.jraft.rhea.cmd.store.BaseRequest;
import com.hxg.sofa.jraft.rhea.errors.Errors;
import com.hxg.sofa.jraft.rhea.options.RpcOptions;

/**
 * RheaKV's rpc client for sending kv requests and receiving kv responses.
 *
 */
public interface RheaKVRpcService extends Lifecycle<RpcOptions> {

    /**
     * @see #callAsyncWithRpc(BaseRequest, FailoverClosure, Errors, boolean)
     */
    <V> CompletableFuture<V> callAsyncWithRpc(final BaseRequest request, final FailoverClosure<V> closure,
                                              final Errors lastCause);

    /**
     * Send KV requests to the remote data service nodes.
     *
     * @param request       request data
     * @param closure       callback for failover strategy
     * @param lastCause     the exception information held by the last call
     *                      failed, the initial value is null
     * @param requireLeader if true, then request to call the leader node
     * @param <V>           the type of response
     * @return a future with response
     */
    <V> CompletableFuture<V> callAsyncWithRpc(final BaseRequest request, final FailoverClosure<V> closure,
                                              final Errors lastCause, final boolean requireLeader);
}

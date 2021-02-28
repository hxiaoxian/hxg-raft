package com.hxg.sofa.jraft.rhea.client.pd;

import java.util.concurrent.CompletableFuture;

import com.hxg.sofa.jraft.Lifecycle;
import com.hxg.sofa.jraft.rhea.client.failover.FailoverClosure;
import com.hxg.sofa.jraft.rhea.cmd.pd.BaseRequest;
import com.hxg.sofa.jraft.rhea.errors.Errors;
import com.hxg.sofa.jraft.rhea.options.RpcOptions;

/**
 * Placement driver's rpc client for sending requests and receiving responses.
 *
 */
public interface PlacementDriverRpcService extends Lifecycle<RpcOptions> {

    /**
     * Send requests to the remote placement driver nodes.
     *
     * @param request   request data
     * @param closure   callback for failover strategy
     * @param lastCause the exception information held by the last call
     *                  failed, the initial value is null
     * @param <V>       the type of response
     * @return a future with response
     */
    <V> CompletableFuture<V> callPdServerWithRpc(final BaseRequest request, final FailoverClosure<V> closure,
                                                 final Errors lastCause);
}

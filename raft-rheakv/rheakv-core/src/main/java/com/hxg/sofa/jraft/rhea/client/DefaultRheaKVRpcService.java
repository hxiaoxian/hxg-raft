package com.hxg.sofa.jraft.rhea.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import com.hxg.sofa.jraft.rhea.client.failover.FailoverClosure;
import com.hxg.sofa.jraft.rhea.client.pd.AbstractPlacementDriverClient;
import com.hxg.sofa.jraft.rhea.client.pd.PlacementDriverClient;
import com.hxg.sofa.jraft.rhea.cmd.store.BaseRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.BaseResponse;
import com.hxg.sofa.jraft.rhea.errors.Errors;
import com.hxg.sofa.jraft.rhea.errors.ErrorsHelper;
import com.hxg.sofa.jraft.rhea.options.RpcOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxg.sofa.jraft.Status;
import com.hxg.sofa.jraft.rhea.rpc.ExtSerializerSupports;
import com.hxg.sofa.jraft.rhea.util.concurrent.CallerRunsPolicyWithReport;
import com.hxg.sofa.jraft.rhea.util.concurrent.NamedThreadFactory;
import com.hxg.sofa.jraft.rpc.InvokeCallback;
import com.hxg.sofa.jraft.rpc.InvokeContext;
import com.hxg.sofa.jraft.rpc.RpcClient;
import com.hxg.sofa.jraft.rpc.impl.BoltRpcClient;
import com.hxg.sofa.jraft.util.Endpoint;
import com.hxg.sofa.jraft.util.ExecutorServiceHelper;
import com.hxg.sofa.jraft.util.Requires;
import com.hxg.sofa.jraft.util.ThreadPoolUtil;

/**
 *
 */
public class DefaultRheaKVRpcService implements RheaKVRpcService {

    private static final Logger         LOG = LoggerFactory.getLogger(DefaultRheaKVRpcService.class);

    private final PlacementDriverClient pdClient;
    private final RpcClient             rpcClient;
    private final Endpoint              selfEndpoint;

    private ThreadPoolExecutor          rpcCallbackExecutor;
    private int                         rpcTimeoutMillis;

    private boolean                     started;

    public DefaultRheaKVRpcService(PlacementDriverClient pdClient, Endpoint selfEndpoint) {
        this.pdClient = pdClient;
        this.rpcClient = ((AbstractPlacementDriverClient) pdClient).getRpcClient();
        this.selfEndpoint = selfEndpoint;
    }

    @Override
    public synchronized boolean init(final RpcOptions opts) {
        if (this.started) {
            LOG.info("[DefaultRheaKVRpcService] already started.");
            return true;
        }
        this.rpcCallbackExecutor = createRpcCallbackExecutor(opts);
        this.rpcTimeoutMillis = opts.getRpcTimeoutMillis();
        Requires.requireTrue(this.rpcTimeoutMillis > 0, "opts.rpcTimeoutMillis must > 0");
        LOG.info("[DefaultRheaKVRpcService] start successfully, options: {}.", opts);
        return this.started = true;
    }

    @Override
    public synchronized void shutdown() {
        ExecutorServiceHelper.shutdownAndAwaitTermination(this.rpcCallbackExecutor);
        this.started = false;
        LOG.info("[DefaultRheaKVRpcService] shutdown successfully.");
    }

    @Override
    public <V> CompletableFuture<V> callAsyncWithRpc(final BaseRequest request, final FailoverClosure<V> closure,
                                                     final Errors lastCause) {
        return callAsyncWithRpc(request, closure, lastCause, true);
    }

    @Override
    public <V> CompletableFuture<V> callAsyncWithRpc(final BaseRequest request, final FailoverClosure<V> closure,
                                                     final Errors lastCause, final boolean requireLeader) {
        final boolean forceRefresh = ErrorsHelper.isInvalidPeer(lastCause);
        final Endpoint endpoint = getRpcEndpoint(request.getRegionId(), forceRefresh, this.rpcTimeoutMillis,
            requireLeader);
        internalCallAsyncWithRpc(endpoint, request, closure);
        return closure.future();
    }

    public Endpoint getLeader(final long regionId, final boolean forceRefresh, final long timeoutMillis) {
        return this.pdClient.getLeader(regionId, forceRefresh, timeoutMillis);
    }

    public Endpoint getLuckyPeer(final long regionId, final boolean forceRefresh, final long timeoutMillis) {
        return this.pdClient.getLuckyPeer(regionId, forceRefresh, timeoutMillis, this.selfEndpoint);
    }

    public Endpoint getRpcEndpoint(final long regionId, final boolean forceRefresh, final long timeoutMillis,
                                   final boolean requireLeader) {
        if (requireLeader) {
            return getLeader(regionId, forceRefresh, timeoutMillis);
        } else {
            return getLuckyPeer(regionId, forceRefresh, timeoutMillis);
        }
    }

    private <V> void internalCallAsyncWithRpc(final Endpoint endpoint, final BaseRequest request,
                                              final FailoverClosure<V> closure) {
        final InvokeContext invokeCtx = new InvokeContext();
        invokeCtx.put(BoltRpcClient.BOLT_CTX, ExtSerializerSupports.getInvokeContext());
        final InvokeCallback invokeCallback = new InvokeCallback() {

            @Override
            public void complete(final Object result, final Throwable err) {
                if (err == null) {
                    final BaseResponse<?> response = (BaseResponse<?>) result;
                    if (response.isSuccess()) {
                        closure.setData(response.getValue());
                        closure.run(Status.OK());
                    } else {
                        closure.setError(response.getError());
                        closure.run(new Status(-1, "RPC failed with address: %s, response: %s", endpoint, response));
                    }
                } else {
                    closure.failure(err);
                }
            }

            @Override
            public Executor executor() {
                return rpcCallbackExecutor;
            }
        };

        try {
            this.rpcClient.invokeAsync(endpoint, request, invokeCtx, invokeCallback, this.rpcTimeoutMillis);
        } catch (final Throwable t) {
            closure.failure(t);
        }
    }

    private ThreadPoolExecutor createRpcCallbackExecutor(final RpcOptions opts) {
        final int callbackExecutorCorePoolSize = opts.getCallbackExecutorCorePoolSize();
        final int callbackExecutorMaximumPoolSize = opts.getCallbackExecutorMaximumPoolSize();
        if (callbackExecutorCorePoolSize <= 0 || callbackExecutorMaximumPoolSize <= 0) {
            return null;
        }

        final String name = "rheakv-rpc-callback";
        return ThreadPoolUtil.newBuilder() //
            .poolName(name) //
            .enableMetric(true) //
            .coreThreads(callbackExecutorCorePoolSize) //
            .maximumThreads(callbackExecutorMaximumPoolSize) //
            .keepAliveSeconds(120L) //
            .workQueue(new ArrayBlockingQueue<>(opts.getCallbackExecutorQueueCapacity())) //
            .threadFactory(new NamedThreadFactory(name, true)) //
            .rejectedHandler(new CallerRunsPolicyWithReport(name)) //
            .build();
    }
}

package com.hxg.sofa.jraft.rhea;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxg.sofa.jraft.Closure;
import com.hxg.sofa.jraft.Status;
import com.hxg.sofa.jraft.rpc.RpcContext;

/**
 * RPC request processor closure wraps request/response and network biz context.
 *
 */
public class RequestProcessClosure<REQ, RSP> implements Closure {

    private static final Logger                                           LOG           = LoggerFactory
                                                                                            .getLogger(RequestProcessClosure.class);

    private static final AtomicIntegerFieldUpdater<RequestProcessClosure> STATE_UPDATER = AtomicIntegerFieldUpdater
                                                                                            .newUpdater(
                                                                                                RequestProcessClosure.class,
                                                                                                "state");

    private static final int                                              PENDING       = 0;
    private static final int                                              RESPOND       = 1;

    private final REQ                                                     request;
    private final RpcContext                                              rpcCtx;

    private RSP                                                           response;

    private volatile int                                                  state         = PENDING;

    public RequestProcessClosure(REQ request, RpcContext rpcCtx) {
        super();
        this.request = request;
        this.rpcCtx = rpcCtx;
    }

    public RpcContext getRpcCtx() {
        return rpcCtx;
    }

    public REQ getRequest() {
        return request;
    }

    public RSP getResponse() {
        return response;
    }

    public void sendResponse(final RSP response) {
        this.response = response;
        run(null);
    }

    /**
     * Run the closure and send response.
     */
    @Override
    public void run(final Status status) {
        if (!STATE_UPDATER.compareAndSet(this, PENDING, RESPOND)) {
            LOG.warn("A response: {} with status: {} sent repeatedly!", this.response, status);
            return;
        }
        this.rpcCtx.sendResponse(this.response);
    }
}

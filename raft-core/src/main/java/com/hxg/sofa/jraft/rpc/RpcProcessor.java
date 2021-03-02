package com.hxg.sofa.jraft.rpc;

import java.util.concurrent.Executor;

/**
 * Defined functions for process user defined request.
 *
 *
 */
public interface RpcProcessor<T> {

    /**
     * Async to handle request with {@link RpcContext}.
     *
     * @param rpcCtx  the rpc context
     * @param request the request
     */
    void handleRequest(final RpcContext rpcCtx, final T request);

    /**
     * The class name of user request.
     * Use String type to avoid loading class.
     *
     * @return interested request's class name
     */
    String interest();

    /**
     * Get user's executor.
     *
     * @return executor
     */
    default Executor executor() {
        return null;
    }

    /**
     *
     * @return the executor selector
     */
    default ExecutorSelector executorSelector() {
        return null;
    }

    /**
     * Executor selector interface.
     */
    interface ExecutorSelector {

        /**
         * Select a executor.
         *
         * @param reqClass  request class name
         * @param reqHeader request header
         * @return a executor
         */
        Executor select(final String reqClass, final Object reqHeader);
    }
}

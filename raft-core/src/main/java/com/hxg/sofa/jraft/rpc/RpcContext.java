package com.hxg.sofa.jraft.rpc;

  
public interface RpcContext {

    /**
     * Send a response back.
     *
     * @param responseObj the response object
     */
    void sendResponse(final Object responseObj);

    /**
     * Get current connection.
     *
     * @return current connection
     */
    Connection getConnection();

    /**
     * GFet the remote address.
     *
     * @return remote address
     */
    String getRemoteAddress();
}

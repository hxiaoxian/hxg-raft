package com.hxg.sofa.jraft.rpc;

import com.hxg.sofa.jraft.Lifecycle;
import com.hxg.sofa.jraft.rpc.impl.ConnectionClosedEventListener;

  
public interface RpcServer extends Lifecycle<Void> {

    /**
     * Register a conn closed event listener.
     *
     * @param listener the event listener.
     */
    void registerConnectionClosedEventListener(final ConnectionClosedEventListener listener);

    /**
     * Register user processor.
     *
     * @param processor the user processor which has a interest
     */
    void registerProcessor(final RpcProcessor<?> processor);

    /**
     *
     * @return bound port
     */
    int boundPort();
}

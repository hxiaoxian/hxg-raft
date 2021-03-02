package com.hxg.sofa.jraft.rpc.impl;

import com.hxg.sofa.jraft.rpc.Connection;


public interface ConnectionClosedEventListener {

    void onClosed(final String remoteAddress, final Connection conn);
}

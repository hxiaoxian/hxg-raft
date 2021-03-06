/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hxg.sofa.jraft.rpc.impl.cli;

import java.util.concurrent.Future;

import com.hxg.sofa.jraft.option.CliOptions;
import com.hxg.sofa.jraft.option.RpcOptions;
import com.hxg.sofa.jraft.rpc.CliClientService;
import com.hxg.sofa.jraft.rpc.CliRequests;
import com.hxg.sofa.jraft.rpc.CliRequests.AddLearnersRequest;
import com.hxg.sofa.jraft.rpc.CliRequests.AddPeerRequest;
import com.hxg.sofa.jraft.rpc.CliRequests.AddPeerResponse;
import com.hxg.sofa.jraft.rpc.CliRequests.ChangePeersRequest;
import com.hxg.sofa.jraft.rpc.CliRequests.ChangePeersResponse;
import com.hxg.sofa.jraft.rpc.CliRequests.GetLeaderRequest;
import com.hxg.sofa.jraft.rpc.CliRequests.GetLeaderResponse;
import com.hxg.sofa.jraft.rpc.CliRequests.LearnersOpResponse;
import com.hxg.sofa.jraft.rpc.CliRequests.RemoveLearnersRequest;
import com.hxg.sofa.jraft.rpc.CliRequests.RemovePeerRequest;
import com.hxg.sofa.jraft.rpc.CliRequests.RemovePeerResponse;
import com.hxg.sofa.jraft.rpc.CliRequests.ResetLearnersRequest;
import com.hxg.sofa.jraft.rpc.CliRequests.ResetPeerRequest;
import com.hxg.sofa.jraft.rpc.CliRequests.SnapshotRequest;
import com.hxg.sofa.jraft.rpc.CliRequests.TransferLeaderRequest;
import com.hxg.sofa.jraft.rpc.RpcRequests.ErrorResponse;
import com.hxg.sofa.jraft.rpc.RpcResponseClosure;
import com.hxg.sofa.jraft.rpc.impl.AbstractClientService;
import com.hxg.sofa.jraft.util.Endpoint;
import com.google.protobuf.Message;

/**
 *
 *     
 *
 */
public class CliClientServiceImpl extends AbstractClientService implements CliClientService {

    private CliOptions cliOptions;

    @Override
    public synchronized boolean init(final RpcOptions rpcOptions) {
        boolean ret = super.init(rpcOptions);
        if (ret) {
            this.cliOptions = (CliOptions) this.rpcOptions;
        }
        return ret;
    }

    @Override
    public Future<Message> addPeer(final Endpoint endpoint, final AddPeerRequest request,
                                   final RpcResponseClosure<AddPeerResponse> done) {
        return invokeWithDone(endpoint, request, done, this.cliOptions.getTimeoutMs());
    }

    @Override
    public Future<Message> removePeer(final Endpoint endpoint, final RemovePeerRequest request,
                                      final RpcResponseClosure<RemovePeerResponse> done) {
        return invokeWithDone(endpoint, request, done, this.cliOptions.getTimeoutMs());
    }

    @Override
    public Future<Message> resetPeer(final Endpoint endpoint, final ResetPeerRequest request,
                                     final RpcResponseClosure<ErrorResponse> done) {
        return invokeWithDone(endpoint, request, done, this.cliOptions.getTimeoutMs());
    }

    @Override
    public Future<Message> snapshot(final Endpoint endpoint, final SnapshotRequest request,
                                    final RpcResponseClosure<ErrorResponse> done) {
        return invokeWithDone(endpoint, request, done, this.cliOptions.getTimeoutMs());
    }

    @Override
    public Future<Message> changePeers(final Endpoint endpoint, final ChangePeersRequest request,
                                       final RpcResponseClosure<ChangePeersResponse> done) {
        return invokeWithDone(endpoint, request, done, this.cliOptions.getTimeoutMs());
    }

    @Override
    public Future<Message> addLearners(final Endpoint endpoint, final AddLearnersRequest request,
                                       final RpcResponseClosure<LearnersOpResponse> done) {
        return invokeWithDone(endpoint, request, done, this.cliOptions.getTimeoutMs());
    }

    @Override
    public Future<Message> removeLearners(final Endpoint endpoint, final RemoveLearnersRequest request,
                                          final RpcResponseClosure<LearnersOpResponse> done) {
        return invokeWithDone(endpoint, request, done, this.cliOptions.getTimeoutMs());
    }

    @Override
    public Future<Message> resetLearners(final Endpoint endpoint, final ResetLearnersRequest request,
                                         final RpcResponseClosure<LearnersOpResponse> done) {
        return invokeWithDone(endpoint, request, done, this.cliOptions.getTimeoutMs());
    }

    @Override
    public Future<Message> getLeader(final Endpoint endpoint, final GetLeaderRequest request,
                                     final RpcResponseClosure<GetLeaderResponse> done) {
        return invokeWithDone(endpoint, request, done, this.cliOptions.getTimeoutMs());
    }

    @Override
    public Future<Message> transferLeader(final Endpoint endpoint, final TransferLeaderRequest request,
                                          final RpcResponseClosure<ErrorResponse> done) {
        return invokeWithDone(endpoint, request, done, this.cliOptions.getTimeoutMs());
    }

    @Override
    public Future<Message> getPeers(final Endpoint endpoint, final CliRequests.GetPeersRequest request,
                                    final RpcResponseClosure<CliRequests.GetPeersResponse> done) {
        return invokeWithDone(endpoint, request, done, this.cliOptions.getTimeoutMs());
    }
}

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
package com.hxg.sofa.jraft.rpc.impl.core;

import com.hxg.sofa.jraft.entity.PeerId;
import com.hxg.sofa.jraft.rpc.RaftServerService;
import com.hxg.sofa.jraft.rpc.RpcRequests;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;

public class ReadIndexRequestProcessorTest extends BaseNodeRequestProcessorTest<RpcRequests.ReadIndexRequest> {
    private RpcRequests.ReadIndexRequest request;

    @Override
    public RpcRequests.ReadIndexRequest createRequest(String groupId, PeerId peerId) {
        request = RpcRequests.ReadIndexRequest.newBuilder().setGroupId(groupId). //
            setServerId("localhostL8082"). //
            setPeerId(peerId.toString()). //
            build();
        return request;
    }

    @Override
    public NodeRequestProcessor<RpcRequests.ReadIndexRequest> newProcessor() {
        return new ReadIndexRequestProcessor(null);
    }

    @Override
    public void verify(String interest, RaftServerService service, NodeRequestProcessor<RpcRequests.ReadIndexRequest> processor) {
        assertEquals(interest, RpcRequests.ReadIndexRequest.class.getName());
        Mockito.verify(service).handleReadIndexRequest(eq(request), Mockito.any());
    }

}

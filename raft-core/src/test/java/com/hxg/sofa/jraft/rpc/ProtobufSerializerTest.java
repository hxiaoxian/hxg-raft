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
package com.hxg.sofa.jraft.rpc;

import com.hxg.sofa.jraft.Status;
import com.hxg.sofa.jraft.test.TestUtils;
import com.hxg.sofa.jraft.util.RpcFactoryHelper;
import org.junit.Test;

import com.alipay.remoting.rpc.RpcCommandFactory;
import com.alipay.remoting.rpc.protocol.RpcRequestCommand;
import com.alipay.remoting.rpc.protocol.RpcResponseCommand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ProtobufSerializerTest {

    private final ProtobufSerializer serializer = ProtobufSerializer.INSTANCE;

    final RpcCommandFactory          cmdFactory = new RpcCommandFactory();

    @Test
    public void testEncodeDecodeRequestContent() throws Exception {
        final RpcRequests.PingRequest reqObject = TestUtils.createPingRequest();
        final RpcRequestCommand request = cmdFactory.createRequestCommand(reqObject);
        request.setRequestClass(RpcRequests.PingRequest.class.getName());
        assertTrue(serializer.serializeContent(request, null));

        request.setRequestObject(null);
        assertTrue(serializer.deserializeContent(request));
        assertNotNull(request.getRequestObject());
        assertEquals(reqObject, request.getRequestObject());
        assertNotSame(reqObject, request.getRequestObject());
    }

    @Test
    public void testEncodeDecodeAppendEntiresRequestHeader() throws Exception {
        final RpcRequests.AppendEntriesRequest reqObject = RpcRequests.AppendEntriesRequest.newBuilder() //
            .setGroupId("testGroup") //
            .setPeerId("testPeer")//
            .setServerId("testServer") //
            .setTerm(1)//
            .setPrevLogIndex(1)//
            .setPrevLogTerm(0) //
            .setCommittedIndex(1).build();
        final RpcCommandFactory cmdFactory = new RpcCommandFactory();
        final RpcRequestCommand request = cmdFactory.createRequestCommand(reqObject);
        request.setRequestClass(RpcRequests.AppendEntriesRequest.class.getName());
        assertNull(request.getHeader());
        assertTrue(serializer.serializeContent(request, null));
        assertTrue(serializer.serializeHeader(request, null));
        assertNull(request.getRequestHeader());

        request.setRequestObject(null);
        assertTrue(serializer.deserializeContent(request));
        assertTrue(serializer.deserializeHeader(request));
        assertNotNull(request.getRequestObject());
        assertNotNull(request.getRequestHeader());

        assertEquals(reqObject, request.getRequestObject());
        assertNotSame(reqObject, request.getRequestObject());

        final RpcRequests.AppendEntriesRequestHeader header = (RpcRequests.AppendEntriesRequestHeader) request.getRequestHeader();
        assertEquals("testGroup", header.getGroupId());
        assertEquals("testPeer", header.getPeerId());
        assertEquals("testServer", header.getServerId());

    }

    @Test
    public void testEncodeDecodeResponseContent() throws Exception {
        final RpcRequests.PingRequest reqObject = TestUtils.createPingRequest();
        final RpcRequestCommand request = cmdFactory.createRequestCommand(reqObject);
        final RpcRequests.ErrorResponse respObject = (RpcRequests.ErrorResponse) RpcFactoryHelper.responseFactory().newResponse(null,
            new Status(-1, "test"));
        final RpcResponseCommand response = cmdFactory.createResponse(respObject, request);
        response.setResponseClass(RpcRequests.ErrorResponse.class.getName());
        assertTrue(serializer.serializeContent(response));

        response.setResponseObject(null);
        assertTrue(serializer.deserializeContent(response, null));
        assertNotNull(response.getResponseObject());
        assertEquals(respObject, response.getResponseObject());
        assertNotSame(respObject, response.getResponseObject());
    }
}

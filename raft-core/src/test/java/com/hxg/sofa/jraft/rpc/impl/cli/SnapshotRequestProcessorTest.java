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

import com.hxg.sofa.jraft.Closure;
import com.hxg.sofa.jraft.Node;
import com.hxg.sofa.jraft.entity.PeerId;
import com.hxg.sofa.jraft.rpc.CliRequests;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SnapshotRequestProcessorTest extends AbstractCliRequestProcessorTest<CliRequests.SnapshotRequest> {

    @Override
    public CliRequests.SnapshotRequest createRequest(String groupId, PeerId peerId) {
        return CliRequests.SnapshotRequest.newBuilder().setGroupId(groupId).setPeerId(peerId.toString()).build();

    }

    @Override
    public BaseCliRequestProcessor<CliRequests.SnapshotRequest> newProcessor() {
        return new SnapshotRequestProcessor(null);
    }

    @Override
    public void verify(String interest, Node node, ArgumentCaptor<Closure> doneArg) {
        assertEquals(CliRequests.SnapshotRequest.class.getName(), interest);
        Mockito.verify(node).snapshot(doneArg.capture());
        assertNotNull(doneArg.getValue());
    }

}

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
import com.hxg.sofa.jraft.JRaftUtils;
import com.hxg.sofa.jraft.Node;
import com.hxg.sofa.jraft.Status;
import com.hxg.sofa.jraft.entity.PeerId;
import com.hxg.sofa.jraft.rpc.CliRequests;
import org.junit.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;

public class ChangePeersRequestProcessorTest extends AbstractCliRequestProcessorTest<CliRequests.ChangePeersRequest> {

    @Override
    public CliRequests.ChangePeersRequest createRequest(String groupId, PeerId peerId) {
        return CliRequests.ChangePeersRequest.newBuilder(). //
            setGroupId(groupId). //
            setLeaderId(peerId.toString()). //
            addNewPeers("localhost:8084").addNewPeers("localhost:8085").build();
    }

    @Override
    public BaseCliRequestProcessor<CliRequests.ChangePeersRequest> newProcessor() {
        return new ChangePeersRequestProcessor(null);
    }

    @Override
    public void verify(String interest, Node node, ArgumentCaptor<Closure> doneArg) {
        assertEquals(interest, CliRequests.ChangePeersRequest.class.getName());
        Mockito.verify(node).changePeers(Matchers.eq(JRaftUtils.getConfiguration("localhost:8084,localhost:8085")),
            doneArg.capture());
        Closure done = doneArg.getValue();
        assertNotNull(done);
        done.run(Status.OK());
        assertNotNull(this.asyncContext.getResponseObject());
        Assert.assertEquals("[localhost:8081, localhost:8082, localhost:8083]", this.asyncContext
            .as(CliRequests.ChangePeersResponse.class).getOldPeersList().toString());
        Assert.assertEquals("[localhost:8084, localhost:8085]", this.asyncContext.as(CliRequests.ChangePeersResponse.class)
            .getNewPeersList().toString());
    }

}

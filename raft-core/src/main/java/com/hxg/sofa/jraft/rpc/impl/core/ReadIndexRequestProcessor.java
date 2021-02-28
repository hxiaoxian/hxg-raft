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

import java.util.concurrent.Executor;

import com.hxg.sofa.jraft.Status;
import com.hxg.sofa.jraft.rpc.RaftServerService;
import com.hxg.sofa.jraft.rpc.RpcRequestClosure;
import com.hxg.sofa.jraft.rpc.RpcRequests;
import com.hxg.sofa.jraft.rpc.RpcRequests.ReadIndexRequest;
import com.hxg.sofa.jraft.rpc.RpcResponseClosureAdapter;
import com.google.protobuf.Message;

/**
 * Handle read index request.
 *
 *     
 *
 */
public class ReadIndexRequestProcessor extends NodeRequestProcessor<ReadIndexRequest> {

    public ReadIndexRequestProcessor(Executor executor) {
        super(executor, RpcRequests.ReadIndexResponse.getDefaultInstance());
    }

    @Override
    protected String getPeerId(final ReadIndexRequest request) {
        return request.getPeerId();
    }

    @Override
    protected String getGroupId(final ReadIndexRequest request) {
        return request.getGroupId();
    }

    @Override
    public Message processRequest0(final RaftServerService service, final ReadIndexRequest request,
                                   final RpcRequestClosure done) {
        service.handleReadIndexRequest(request, new RpcResponseClosureAdapter<RpcRequests.ReadIndexResponse>() {

            @Override
            public void run(final Status status) {
                if (getResponse() != null) {
                    done.sendResponse(getResponse());
                } else {
                    done.run(status);
                }
            }

        });
        return null;
    }

    @Override
    public String interest() {
        return ReadIndexRequest.class.getName();
    }
}

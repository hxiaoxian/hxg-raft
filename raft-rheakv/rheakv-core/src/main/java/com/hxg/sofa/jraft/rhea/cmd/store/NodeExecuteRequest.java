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
package com.hxg.sofa.jraft.rhea.cmd.store;

import com.hxg.sofa.jraft.rhea.storage.NodeExecutor;

/**
 *
 * @author jiachun.fjc
 */
public class NodeExecuteRequest extends BaseRequest {

    private static final long serialVersionUID = -9087272065211874819L;

    private NodeExecutor      nodeExecutor;

    public NodeExecutor getNodeExecutor() {
        return nodeExecutor;
    }

    public void setNodeExecutor(NodeExecutor nodeExecutor) {
        this.nodeExecutor = nodeExecutor;
    }

    @Override
    public byte magic() {
        return NODE_EXECUTE;
    }
}
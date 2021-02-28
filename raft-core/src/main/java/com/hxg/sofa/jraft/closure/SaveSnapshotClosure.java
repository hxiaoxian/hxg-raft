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
package com.hxg.sofa.jraft.closure;

import com.hxg.sofa.jraft.Closure;
import com.hxg.sofa.jraft.storage.snapshot.SnapshotWriter;
import com.hxg.sofa.jraft.entity.RaftOutter;

/**
 * Save snapshot closure
 *
 * @author boyan (boyan@alibaba-inc.com)
 *
 * 2018-Apr-04 2:21:30 PM
 */
public interface SaveSnapshotClosure extends Closure {

    /**
     * Starts to save snapshot, returns the writer.
     *
     * @param meta metadata of snapshot.
     * @return returns snapshot writer.
     */
    SnapshotWriter start(final RaftOutter.SnapshotMeta meta);
}
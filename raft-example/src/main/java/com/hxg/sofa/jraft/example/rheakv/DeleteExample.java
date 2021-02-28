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
package com.hxg.sofa.jraft.example.rheakv;

import java.util.concurrent.CompletableFuture;

import com.hxg.sofa.jraft.util.BytesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxg.sofa.jraft.rhea.client.FutureHelper;
import com.hxg.sofa.jraft.rhea.client.RheaKVStore;

/**
 *
 *
 */
public class DeleteExample {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteExample.class);

    public static void main(final String[] args) throws Exception {
        final Client client = new Client();
        client.init();
        delete(client.getRheaKVStore());
        client.shutdown();
    }

    public static void delete(final RheaKVStore rheaKVStore) {
        rheaKVStore.bPut("delete_test", BytesUtil.writeUtf8("1"));
        LOG.info("Value={}", BytesUtil.readUtf8(rheaKVStore.bGet("delete_test")));
        final CompletableFuture<Boolean> f1 = rheaKVStore.delete(BytesUtil.writeUtf8("delete_test"));
        FutureHelper.get(f1);
        LOG.info("Value={}", BytesUtil.readUtf8(rheaKVStore.bGet("delete_test")));

        rheaKVStore.bPut("delete_test", BytesUtil.writeUtf8("1"));
        LOG.info("Value={}", BytesUtil.readUtf8(rheaKVStore.bGet("delete_test")));
        final CompletableFuture<Boolean> f2 = rheaKVStore.delete("delete_test");
        FutureHelper.get(f2);
        LOG.info("Value={}", BytesUtil.readUtf8(rheaKVStore.bGet("delete_test")));

        rheaKVStore.bPut("delete_test", BytesUtil.writeUtf8("1"));
        LOG.info("Value={}", BytesUtil.readUtf8(rheaKVStore.bGet("delete_test")));
        rheaKVStore.bDelete(BytesUtil.writeUtf8("delete_test"));
        LOG.info("Value={}", BytesUtil.readUtf8(rheaKVStore.bGet("delete_test")));

        rheaKVStore.bPut("delete_test", BytesUtil.writeUtf8("1"));
        LOG.info("Value={}", BytesUtil.readUtf8(rheaKVStore.bGet("delete_test")));
        rheaKVStore.bDelete("delete_test");
        LOG.info("Value={}", BytesUtil.readUtf8(rheaKVStore.bGet("delete_test")));
    }
}

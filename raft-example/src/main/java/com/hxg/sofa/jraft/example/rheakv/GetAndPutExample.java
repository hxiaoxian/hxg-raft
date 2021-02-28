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
 * @author jiachun.fjc
 */
public class GetAndPutExample {

    private static final Logger LOG = LoggerFactory.getLogger(GetAndPutExample.class);

    public static void main(final String[] args) throws Exception {
        final Client client = new Client();
        client.init();
        put(client.getRheaKVStore());
        client.shutdown();
    }

    public static void put(final RheaKVStore rheaKVStore) {
        final CompletableFuture<byte[]> f1 = rheaKVStore.getAndPut(BytesUtil.writeUtf8("getAndPut"), BytesUtil.writeUtf8("getAndPutValue"));
        LOG.info("Old value: {}", BytesUtil.readUtf8(FutureHelper.get(f1)));

        final CompletableFuture<byte[]> f2 = rheaKVStore.getAndPut("getAndPut", BytesUtil.writeUtf8("getAndPutValue2"));
        LOG.info("Old value: {}", BytesUtil.readUtf8(FutureHelper.get(f2)));

        final byte[] b1 = rheaKVStore.bGetAndPut(BytesUtil.writeUtf8("getAndPut1"), BytesUtil.writeUtf8("getAndPutValue3"));
        LOG.info("Old value: {}", BytesUtil.readUtf8(b1));

        final byte[] b2 = rheaKVStore.bGetAndPut(BytesUtil.writeUtf8("getAndPut1"), BytesUtil.writeUtf8("getAndPutValue4"));
        LOG.info("Old value: {}", BytesUtil.readUtf8(b2));
    }
}
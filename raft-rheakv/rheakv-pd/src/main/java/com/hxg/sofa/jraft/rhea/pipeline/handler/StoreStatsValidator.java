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
package com.hxg.sofa.jraft.rhea.pipeline.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxg.sofa.jraft.rhea.MetadataStore;
import com.hxg.sofa.jraft.rhea.cmd.pd.StoreHeartbeatRequest;
import com.hxg.sofa.jraft.rhea.errors.Errors;
import com.hxg.sofa.jraft.rhea.metadata.StoreStats;
import com.hxg.sofa.jraft.rhea.metadata.TimeInterval;
import com.hxg.sofa.jraft.rhea.pipeline.event.StorePingEvent;
import com.hxg.sofa.jraft.rhea.util.pipeline.Handler;
import com.hxg.sofa.jraft.rhea.util.pipeline.HandlerContext;
import com.hxg.sofa.jraft.rhea.util.pipeline.InboundHandlerAdapter;
import com.hxg.sofa.jraft.util.SPI;

/**
 *
 *
 */
@SPI(name = "storeStatsValidator", priority = 100)
@Handler.Sharable
public class StoreStatsValidator extends InboundHandlerAdapter<StorePingEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(StoreStatsValidator.class);

    @Override
    public void readMessage(final HandlerContext ctx, final StorePingEvent event) throws Exception {
        final MetadataStore metadataStore = event.getMetadataStore();
        final StoreHeartbeatRequest request = event.getMessage();
        final StoreStats storeStats = request.getStats();
        if (storeStats == null) {
            LOG.error("Empty [StoreStats] by event: {}.", event);
            throw Errors.INVALID_STORE_STATS.exception();
        }
        final StoreStats currentStoreStats = metadataStore.getStoreStats(request.getClusterId(),
            storeStats.getStoreId());
        if (currentStoreStats == null) {
            return; // new data
        }
        final TimeInterval interval = storeStats.getInterval();
        if (interval == null) {
            LOG.error("Empty [TimeInterval] by event: {}.", event);
            throw Errors.INVALID_STORE_STATS.exception();
        }
        final TimeInterval currentInterval = currentStoreStats.getInterval();
        if (interval.getEndTimestamp() < currentInterval.getEndTimestamp()) {
            LOG.error("The [TimeInterval] is out of date: {}.", event);
            throw Errors.STORE_HEARTBEAT_OUT_OF_DATE.exception();
        }
    }
}

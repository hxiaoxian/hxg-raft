package com.hxg.sofa.jraft.rhea.client.pd;

import java.util.List;

import com.hxg.sofa.jraft.rhea.metadata.Region;
import com.hxg.sofa.jraft.rhea.metadata.Store;
import com.hxg.sofa.jraft.rhea.options.PlacementDriverOptions;
import com.hxg.sofa.jraft.rhea.options.RegionEngineOptions;
import com.hxg.sofa.jraft.rhea.options.StoreEngineOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxg.sofa.jraft.rhea.util.Lists;
import com.hxg.sofa.jraft.util.Endpoint;

/**
 * Single raft group, no need for a real PD role.
 *
 */
public class FakePlacementDriverClient extends AbstractPlacementDriverClient {

    private static final Logger LOG = LoggerFactory.getLogger(FakePlacementDriverClient.class);

    private boolean             started;

    public FakePlacementDriverClient(long clusterId, String clusterName) {
        super(clusterId, clusterName);
    }

    @Override
    public synchronized boolean init(final PlacementDriverOptions opts) {
        if (this.started) {
            LOG.info("[FakePlacementDriverClient] already started.");
            return true;
        }
        super.init(opts);
        LOG.info("[FakePlacementDriverClient] start successfully, options: {}.", opts);
        return this.started = true;
    }

    @Override
    public synchronized void shutdown() {
        super.shutdown();
        LOG.info("[FakePlacementDriverClient] shutdown successfully.");
    }

    @Override
    protected void refreshRouteTable() {
        // NO-OP
    }

    @Override
    public Store getStoreMetadata(final StoreEngineOptions opts) {
        final Store store = new Store();
        final List<RegionEngineOptions> rOptsList = opts.getRegionEngineOptionsList();
        final List<Region> regionList = Lists.newArrayListWithCapacity(rOptsList.size());
        store.setId(-1);
        store.setEndpoint(opts.getServerAddress());
        for (final RegionEngineOptions rOpts : rOptsList) {
            regionList.add(getLocalRegionMetadata(rOpts));
        }
        store.setRegions(regionList);
        return store;
    }

    @Override
    public Endpoint getPdLeader(final boolean forceRefresh, final long timeoutMillis) {
        throw new UnsupportedOperationException();
    }
}

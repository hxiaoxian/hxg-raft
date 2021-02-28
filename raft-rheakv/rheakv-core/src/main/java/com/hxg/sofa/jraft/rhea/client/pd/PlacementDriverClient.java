package com.hxg.sofa.jraft.rhea.client.pd;

import java.util.List;
import java.util.Map;

import com.hxg.sofa.jraft.Lifecycle;
import com.hxg.sofa.jraft.rhea.client.RegionRouteTable;
import com.hxg.sofa.jraft.rhea.metadata.Peer;
import com.hxg.sofa.jraft.rhea.metadata.Region;
import com.hxg.sofa.jraft.rhea.metadata.Store;
import com.hxg.sofa.jraft.rhea.options.PlacementDriverOptions;
import com.hxg.sofa.jraft.rhea.options.StoreEngineOptions;
import com.hxg.sofa.jraft.rhea.storage.KVEntry;
import com.hxg.sofa.jraft.util.Endpoint;

/**
 * Placement driver client
 *
 */
public interface PlacementDriverClient extends Lifecycle<PlacementDriverOptions> {

    /**
     * Returns the cluster id.
     */
    long getClusterId();

    /**
     * Query the region by region id.
     */
    Region getRegionById(final long regionId);

    /**
     * Returns the region to which the key belongs.
     */
    Region findRegionByKey(final byte[] key, final boolean forceRefresh);

    /**
     * Returns the regions to which the keys belongs.
     */
    Map<Region, List<byte[]>> findRegionsByKeys(final List<byte[]> keys, final boolean forceRefresh);

    /**
     * Returns the regions to which the keys belongs.
     */
    Map<Region, List<KVEntry>> findRegionsByKvEntries(final List<KVEntry> kvEntries, final boolean forceRefresh);

    /**
     * Returns the list of regions covered by startKey and endKey.
     */
    List<Region> findRegionsByKeyRange(final byte[] startKey, final byte[] endKey, final boolean forceRefresh);

    /**
     * Returns the startKey of next region.
     */
    byte[] findStartKeyOfNextRegion(final byte[] key, final boolean forceRefresh);

    /**
     * Returns the regionRouteTable instance.
     */
    RegionRouteTable getRegionRouteTable();

    /**
     * Returns the store metadata of the current instance's store.
     * Construct initial data based on the configuration file if
     * the data on {@link PlacementDriverClient} is empty.
     */
    Store getStoreMetadata(final StoreEngineOptions opts);

    /**
     * Get the specified region leader communication address.
     */
    Endpoint getLeader(final long regionId, final boolean forceRefresh, final long timeoutMillis);

    /**
     * Get the specified region random peer communication address,
     * format: [ip:port]
     */
    Endpoint getLuckyPeer(final long regionId, final boolean forceRefresh, final long timeoutMillis,
                          final Endpoint unExpect);

    /**
     * Refresh the routing information of the specified region
     */
    void refreshRouteConfiguration(final long regionId);

    /**
     * Transfer leader to specified peer.
     */
    boolean transferLeader(final long regionId, final Peer peer, final boolean refreshConf);

    /**
     * Join the specified region group.
     */
    boolean addReplica(final long regionId, final Peer peer, final boolean refreshConf);

    /**
     * Depart from the specified region group.
     */
    boolean removeReplica(final long regionId, final Peer peer, final boolean refreshConf);

    /**
     * Returns raft cluster prefix id.
     */
    String getClusterName();

    /**
     * Get the placement driver server's leader communication address.
     */
    Endpoint getPdLeader(final boolean forceRefresh, final long timeoutMillis);

    /**
     * Returns the pd rpc service client.
     */
    PlacementDriverRpcService getPdRpcService();
}

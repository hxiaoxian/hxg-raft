package com.hxg.sofa.jraft.rhea.client;

import com.hxg.sofa.jraft.Lifecycle;
import com.hxg.sofa.jraft.Status;
import com.hxg.sofa.jraft.conf.Configuration;
import com.hxg.sofa.jraft.option.CliOptions;

/**
 * RheaKV client command-line service.
 *
 */
public interface RheaKVCliService extends Lifecycle<CliOptions> {

    /**
     * Send a split instruction to the specified region.
     *
     * @param regionId    region id
     * @param newRegionId id of the new region after splitting
     * @param groupId     the raft group id
     * @param conf        current configuration
     * @return operation status
     */
    Status rangeSplit(final long regionId, final long newRegionId, final String groupId, final Configuration conf);
}

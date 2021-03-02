package com.hxg.sofa.jraft;

import com.hxg.sofa.jraft.core.CliServiceImpl;
import com.hxg.sofa.jraft.core.NodeImpl;
import com.hxg.sofa.jraft.entity.PeerId;
import com.hxg.sofa.jraft.option.CliOptions;
import com.hxg.sofa.jraft.option.NodeOptions;

/**
 * Service factory to create raft services, such as Node/CliService etc.
 *
 *
 *
 * 2018-May-03 11:06:02 AM
 */
public final class RaftServiceFactory {

    /**
     * Create a raft node with group id and it's serverId.
     */
    public static Node createRaftNode(final String groupId, final PeerId serverId) {
        return new NodeImpl(groupId, serverId);
    }

    /**
     * Create and initialize a raft node with node options.
     * Throw {@link IllegalStateException} when fail to initialize.
     */
    public static Node createAndInitRaftNode(final String groupId, final PeerId serverId, final NodeOptions opts) {
        final Node ret = createRaftNode(groupId, serverId);
        if (!ret.init(opts)) {
            throw new IllegalStateException("Fail to init node, please see the logs to find the reason.");
        }
        return ret;
    }

    /**
     * Create a {@link CliService} instance.
     */
    public static CliService createCliService() {
        return new CliServiceImpl();
    }

    /**
     * Create and initialize a CliService instance.
     */
    public static CliService createAndInitCliService(final CliOptions cliOptions) {
        final CliService ret = createCliService();
        if (!ret.init(cliOptions)) {
            throw new IllegalStateException("Fail to init CliService");
        }
        return ret;
    }
}

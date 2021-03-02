package com.hxg.sofa.jraft.rhea;

import com.hxg.sofa.jraft.entity.PeerId;

/**
 * Follower state listener.
 *
 */
public interface FollowerStateListener extends StateListener {

    default void onLeaderStart(final long term) {
        // NO-OP
    }

    default void onLeaderStop(final long term) {
        // NO-OP
    }

    void onStartFollowing(final PeerId newLeaderId, final long newTerm);

    void onStopFollowing(final PeerId oldLeaderId, final long oldTerm);
}

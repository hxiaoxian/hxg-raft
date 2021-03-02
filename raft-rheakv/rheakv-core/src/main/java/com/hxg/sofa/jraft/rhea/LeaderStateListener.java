package com.hxg.sofa.jraft.rhea;

import com.hxg.sofa.jraft.entity.PeerId;

/**
 * Leader state listener.
 *
 */
public interface LeaderStateListener extends StateListener {

    void onLeaderStart(final long newTerm);

    void onLeaderStop(final long oldTerm);


    default void onStartFollowing(final PeerId newLeaderId, final long newTerm) {
        // NO-OP
    }

    default void onStopFollowing(final PeerId oldLeaderId, final long oldTerm) {
        // NO-OP
    }
}

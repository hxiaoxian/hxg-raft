package com.hxg.sofa.jraft.rhea;

import com.hxg.sofa.jraft.entity.PeerId;

/**
 * The raft state listener.
 *
 */
public interface StateListener {


    void onLeaderStart(final long newTerm);

    void onLeaderStop(final long oldTerm);

    void onStartFollowing(final PeerId newLeaderId, final long newTerm);

    void onStopFollowing(final PeerId oldLeaderId, final long oldTerm);
}

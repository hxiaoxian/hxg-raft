package com.hxg.sofa.jraft;

import java.util.List;

import com.hxg.sofa.jraft.closure.ReadIndexClosure;
import com.hxg.sofa.jraft.conf.Configuration;
import com.hxg.sofa.jraft.core.NodeMetrics;
import com.hxg.sofa.jraft.core.Replicator;
import com.hxg.sofa.jraft.entity.NodeId;
import com.hxg.sofa.jraft.entity.PeerId;
import com.hxg.sofa.jraft.entity.Task;
import com.hxg.sofa.jraft.entity.UserLog;
import com.hxg.sofa.jraft.error.LogIndexOutOfBoundsException;
import com.hxg.sofa.jraft.error.LogNotFoundException;
import com.hxg.sofa.jraft.option.NodeOptions;
import com.hxg.sofa.jraft.option.RaftOptions;
import com.hxg.sofa.jraft.util.Describer;


public interface Node extends Lifecycle<NodeOptions>, Describer {

    PeerId getLeaderId();

    NodeId getNodeId();

    NodeMetrics getNodeMetrics();

    String getGroupId();

    NodeOptions getOptions();

    RaftOptions getRaftOptions();

    boolean isLeader();

    boolean isLeader(final boolean blocking);

    void shutdown(final Closure done);

    void join() throws InterruptedException;

    void apply(final Task task);

    void readIndex(final byte[] requestContext, final ReadIndexClosure done);

    List<PeerId> listPeers();


    List<PeerId> listAlivePeers();


    List<PeerId> listLearners();


    List<PeerId> listAliveLearners();


    void addPeer(final PeerId peer, final Closure done);


    void removePeer(final PeerId peer, final Closure done);


    void changePeers(final Configuration newPeers, final Closure done);


    Status resetPeers(final Configuration newPeers);


    void addLearners(final List<PeerId> learners, final Closure done);

    /**
     * Remove some learners from the raft group. done.run() will be invoked after this
     * operation finishes, describing the detailed result.
     *
     * @param learners learners to remove
     * @param done     callback
     * @since 1.3.0
     */
    void removeLearners(final List<PeerId> learners, final Closure done);

    /**
     * Reset learners in the raft group. done.run() will be invoked after this
     * operation finishes, describing the detailed result.
     *
     * @param learners learners to set
     * @param done     callback
     * @since 1.3.0
     */
    void resetLearners(final List<PeerId> learners, final Closure done);

    /**
     * Start a snapshot immediately if possible. done.run() would be invoked when
     * the snapshot finishes, describing the detailed result.
     *
     * @param done callback
     */
    void snapshot(final Closure done);

    /**
     * Reset the election_timeout for the every node.
     *
     * @param electionTimeoutMs the timeout millis of election
     */
    void resetElectionTimeoutMs(final int electionTimeoutMs);

    /**
     * Try transferring leadership to |peer|. If peer is ANY_PEER, a proper follower
     * will be chosen as the leader for the next term.
     * Returns 0 on success, -1 otherwise.
     *
     * @param peer the target peer of new leader
     * @return operation status
     */
    Status transferLeadershipTo(final PeerId peer);

    /**
     * Read the first committed user log from the given index.
     *   Return OK on success and user_log is assigned with the very data. Be awared
     *   that the user_log may be not the exact log at the given index, but the
     *   first available user log from the given index to lastCommittedIndex.
     *   Otherwise, appropriate errors are returned:
     *        - return ELOGDELETED when the log has been deleted;
     *        - return ENOMOREUSERLOG when we can't get a user log even reaching lastCommittedIndex.
     * [NOTE] in consideration of safety, we use lastAppliedIndex instead of lastCommittedIndex
     * in code implementation.
     *
     * @param index log index
     * @return user log entry
     * @throws LogNotFoundException  the user log is deleted at index.
     * @throws LogIndexOutOfBoundsException  the special index is out of bounds.
     */
    UserLog readCommittedUserLog(final long index);

    /**
     * SOFAJRaft users can implement the ReplicatorStateListener interface by themselves.
     * So users can do their own logical operator in this listener when replicator created, destroyed or had some errors.
     *
     * @param replicatorStateListener added ReplicatorStateListener which is implemented by users.
     */
    void addReplicatorStateListener(final Replicator.ReplicatorStateListener replicatorStateListener);

    /**
     * End User can remove their implement the ReplicatorStateListener interface by themselves.
     *
     * @param replicatorStateListener need to remove the ReplicatorStateListener which has been added by users.
     */
    void removeReplicatorStateListener(final Replicator.ReplicatorStateListener replicatorStateListener);

    /**
     * Remove all the ReplicatorStateListeners which have been added by users.
     *
     */
    void clearReplicatorStateListeners();

    /**
     * Get the ReplicatorStateListeners which have been added by users.
     *
     * @return node's replicatorStatueListeners which have been added by users.
     */
    List<Replicator.ReplicatorStateListener> getReplicatorStatueListeners();

    /**
     * Get the node's target election priority value.
     *
     * @return node's target election priority value.
     * @since 1.3.0
     */
    int getNodeTargetPriority();
}

package com.hxg.sofa.jraft.rhea;

import java.util.List;

import com.hxg.sofa.jraft.entity.PeerId;
import com.hxg.sofa.jraft.rhea.metadata.Peer;
import com.hxg.sofa.jraft.rhea.util.Lists;
import com.hxg.sofa.jraft.util.Endpoint;
import com.hxg.sofa.jraft.util.Requires;

  
public final class JRaftHelper {

    public static String getJRaftGroupId(final String clusterName, final long regionId) {
        Requires.requireNonNull(clusterName, "clusterName");
        return clusterName + "-" + regionId;
    }

    public static PeerId toJRaftPeerId(final Peer peer) {
        Requires.requireNonNull(peer, "peer");
        final Endpoint endpoint = peer.getEndpoint();
        Requires.requireNonNull(endpoint, "peer.endpoint");
        return new PeerId(endpoint, 0);
    }


    public static Peer toPeer(final PeerId peerId) {
        Requires.requireNonNull(peerId, "peerId");
        final Endpoint endpoint = peerId.getEndpoint();
        Requires.requireNonNull(endpoint, "peerId.endpoint");
        final Peer peer = new Peer();
        peer.setId(-1);
        peer.setStoreId(-1);
        peer.setEndpoint(endpoint.copy());
        return peer;
    }

    public static List<Peer> toPeerList(final List<PeerId> peerIdList) {
        if (peerIdList == null) {
            return null;
        }
        final List<Peer> peerList = Lists.newArrayListWithCapacity(peerIdList.size());
        for (final PeerId peerId : peerIdList) {
            peerList.add(toPeer(peerId));
        }
        return peerList;
    }
}

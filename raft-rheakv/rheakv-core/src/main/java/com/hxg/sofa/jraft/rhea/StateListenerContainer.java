package com.hxg.sofa.jraft.rhea;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.hxg.sofa.jraft.rhea.util.Maps;

/**
 * The container of raft state listener, each key(id) corresponds to a listener group.
 */
public class StateListenerContainer<K> {

    private final ConcurrentMap<K, List<StateListener>> stateListeners = Maps.newConcurrentMap();

    public boolean addStateListener(final K id, final StateListener listener) {
        List<StateListener> group = this.stateListeners.get(id);
        if (group == null) {
            final List<StateListener> newGroup = new CopyOnWriteArrayList<>();
            group = this.stateListeners.putIfAbsent(id, newGroup);
            if (group == null) {
                group = newGroup;
            }
        }
        return group.add(listener);
    }

    public List<StateListener> getStateListenerGroup(final K id) {
        final List<StateListener> group = this.stateListeners.get(id);
        return group == null ? Collections.emptyList() : group;
    }

    public boolean removeStateListener(final K id, final StateListener listener) {
        final List<StateListener> group = this.stateListeners.get(id);
        if (group == null) {
            return false;
        }
        return group.remove(listener);
    }

    public void clear() {
        this.stateListeners.clear();
    }
}

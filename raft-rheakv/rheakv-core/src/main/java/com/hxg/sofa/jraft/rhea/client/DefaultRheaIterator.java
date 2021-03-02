package com.hxg.sofa.jraft.rhea.client;

import java.util.ArrayDeque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import com.hxg.sofa.jraft.rhea.client.pd.PlacementDriverClient;
import com.hxg.sofa.jraft.rhea.storage.KVEntry;
import com.hxg.sofa.jraft.util.BytesUtil;

  
public class DefaultRheaIterator implements RheaIterator<KVEntry> {

    private final DefaultRheaKVStore    rheaKVStore;
    private final PlacementDriverClient pdClient;
    private final byte[]                startKey;
    private final byte[]                endKey;
    private final boolean               readOnlySafe;
    private final boolean               returnValue;
    private final int                   bufSize;
    private final Queue<KVEntry>        buf;

    private byte[]                      cursorKey;

    public DefaultRheaIterator(DefaultRheaKVStore rheaKVStore, byte[] startKey, byte[] endKey, int bufSize,
                               boolean readOnlySafe, boolean returnValue) {
        this.rheaKVStore = rheaKVStore;
        this.pdClient = rheaKVStore.getPlacementDriverClient();
        this.startKey = BytesUtil.nullToEmpty(startKey);
        this.endKey = endKey;
        this.bufSize = bufSize;
        this.readOnlySafe = readOnlySafe;
        this.returnValue = returnValue;
        this.buf = new ArrayDeque<>(bufSize);
        this.cursorKey = this.startKey;
    }

    @Override
    public synchronized boolean hasNext() {
        if (this.buf.isEmpty()) {
            while (this.endKey == null || BytesUtil.compare(this.cursorKey, this.endKey) < 0) {
                final List<KVEntry> kvEntries = this.rheaKVStore.singleRegionScan(this.cursorKey, this.endKey,
                    this.bufSize, this.readOnlySafe, this.returnValue);
                if (kvEntries.isEmpty()) {
                    // cursorKey jump to next region's startKey
                    this.cursorKey = this.pdClient.findStartKeyOfNextRegion(this.cursorKey, false);
                    if (cursorKey == null) { // current is the last region
                        break;
                    }
                } else {
                    final KVEntry last = kvEntries.get(kvEntries.size() - 1);
                    this.cursorKey = BytesUtil.nextBytes(last.getKey()); // cursorKey++
                    this.buf.addAll(kvEntries);
                    break;
                }
            }
            return !this.buf.isEmpty();
        }
        return true;
    }

    @Override
    public synchronized KVEntry next() {
        if (this.buf.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.buf.poll();
    }

    public byte[] getStartKey() {
        return startKey;
    }

    public byte[] getEndKey() {
        return endKey;
    }

    public boolean isReadOnlySafe() {
        return readOnlySafe;
    }

    public int getBufSize() {
        return bufSize;
    }
}

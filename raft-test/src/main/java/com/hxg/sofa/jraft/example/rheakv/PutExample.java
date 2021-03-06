package com.hxg.sofa.jraft.example.rheakv;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.hxg.sofa.jraft.util.BytesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxg.sofa.jraft.rhea.client.FutureHelper;
import com.hxg.sofa.jraft.rhea.client.RheaKVStore;
import com.hxg.sofa.jraft.rhea.storage.KVEntry;
import com.hxg.sofa.jraft.rhea.util.Lists;


public class PutExample {

    private static final Logger LOG = LoggerFactory.getLogger(PutExample.class);

    public static void main(final String[] args) throws Exception {
        final Client client = new Client();
        client.init();
        put(client.getRheaKVStore());
        client.shutdown();
    }

    public static void put(final RheaKVStore rheaKVStore) {
        final byte[] value = BytesUtil.writeUtf8("put_example_value");
        final CompletableFuture<Boolean> r1 = rheaKVStore.put("1", value);
        if (FutureHelper.get(r1)) {
            LOG.info("Async put 1 {} success.", BytesUtil.readUtf8(rheaKVStore.bGet("1")));
        }

        final CompletableFuture<Boolean> r2 = rheaKVStore.put(BytesUtil.writeUtf8("2"), value);
        if (FutureHelper.get(r2)) {
            LOG.info("Async put 2 {} success.", BytesUtil.readUtf8(rheaKVStore.bGet("2")));
        }

        final boolean r3 = rheaKVStore.bPut("3", value);
        if (r3) {
            LOG.info("Sync put 3 {} success.", BytesUtil.readUtf8(rheaKVStore.bGet("3")));
        }

        final boolean r4 = rheaKVStore.bPut(BytesUtil.writeUtf8("4"), value);
        if (r4) {
            LOG.info("Sync put 4 {} success.", BytesUtil.readUtf8(rheaKVStore.bGet("4")));
        }

        // put list
        final KVEntry kv1 = new KVEntry(BytesUtil.writeUtf8("10"), value);
        final KVEntry kv2 = new KVEntry(BytesUtil.writeUtf8("11"), value);
        final KVEntry kv3 = new KVEntry(BytesUtil.writeUtf8("12"), value);
        final KVEntry kv4 = new KVEntry(BytesUtil.writeUtf8("13"), value);
        final KVEntry kv5 = new KVEntry(BytesUtil.writeUtf8("14"), value);

        List<KVEntry> entries = Lists.newArrayList(kv1, kv2, kv3);

        final CompletableFuture<Boolean> r5 = rheaKVStore.put(entries);
        if (FutureHelper.get(r5)) {
            for (final KVEntry entry : entries) {
                LOG.info("Async put list {} with value {} success.", BytesUtil.readUtf8(entry.getKey()),
                    BytesUtil.readUtf8(entry.getValue()));
            }
        }

        entries = Lists.newArrayList(kv3, kv4, kv5);
        final boolean r6 = rheaKVStore.bPut(entries);
        if (r6) {
            for (final KVEntry entry : entries) {
                LOG.info("Sync put list {} with value {} success.", BytesUtil.readUtf8(entry.getKey()),
                    BytesUtil.readUtf8(entry.getValue()));
            }
        }
    }
}

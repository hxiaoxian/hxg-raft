package com.alipay.sofa.jraft.rhea.benchmark;

import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author jiachun.fjc
 */
public class BenchmarkUtil {

    public static final int    CONCURRENCY = 32;
    public static final int    KEY_COUNT   = 1000000;

    public static final byte[] VALUE_BYTES;

    static {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        byte[] bytes = new byte[100];
        random.nextBytes(bytes);
        VALUE_BYTES = bytes;
    }
}

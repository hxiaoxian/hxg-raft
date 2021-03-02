package com.hxg.sofa.jraft.option;

/**
 * Cli service options.
 *
 *
 *
 * 2018-Apr-09 3:25:59 PM
 */
public class CliOptions extends RpcOptions {

    private int timeoutMs;
    private int maxRetry;

    public int getTimeoutMs() {
        return this.timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public int getMaxRetry() {
        return this.maxRetry;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }
}

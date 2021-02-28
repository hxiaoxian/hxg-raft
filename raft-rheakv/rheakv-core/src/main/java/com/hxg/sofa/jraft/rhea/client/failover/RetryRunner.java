package com.hxg.sofa.jraft.rhea.client.failover;

import com.hxg.sofa.jraft.rhea.errors.Errors;

/**
 * A retry task.
 *
 */
public interface RetryRunner {

    void run(final Errors retryCause);
}

package com.hxg.sofa.jraft.rhea;

import com.hxg.sofa.jraft.option.CliOptions;
import com.hxg.sofa.jraft.rhea.client.DefaultRheaKVCliService;
import com.hxg.sofa.jraft.rhea.client.RheaKVCliService;

/**
 * Service factory to create rheaKV services, such as RheaKVCliService etc.
 *
 */
public final class RheaKVServiceFactory {

    /**
     * Create and initialize a RheaKVCliService instance.
     */
    public static RheaKVCliService createAndInitRheaKVCliService(final CliOptions opts) {
        final RheaKVCliService cliService = new DefaultRheaKVCliService();
        if (!cliService.init(opts)) {
            throw new IllegalStateException("Fail to init RheaKVCliService");
        }
        return cliService;
    }

    private RheaKVServiceFactory() {
    }
}

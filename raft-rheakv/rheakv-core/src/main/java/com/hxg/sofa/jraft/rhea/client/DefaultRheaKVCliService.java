package com.hxg.sofa.jraft.rhea.client;

import com.hxg.sofa.jraft.rhea.cmd.store.BaseResponse;
import com.hxg.sofa.jraft.rhea.cmd.store.RangeSplitRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxg.sofa.jraft.CliService;
import com.hxg.sofa.jraft.RaftServiceFactory;
import com.hxg.sofa.jraft.Status;
import com.hxg.sofa.jraft.conf.Configuration;
import com.hxg.sofa.jraft.core.CliServiceImpl;
import com.hxg.sofa.jraft.entity.PeerId;
import com.hxg.sofa.jraft.option.CliOptions;
import com.hxg.sofa.jraft.rhea.util.StackTraceUtil;
import com.hxg.sofa.jraft.rpc.CliClientService;
import com.hxg.sofa.jraft.rpc.RpcClient;
import com.hxg.sofa.jraft.rpc.impl.AbstractClientService;
import com.hxg.sofa.jraft.util.Requires;

/**
 *
 */
public class DefaultRheaKVCliService implements RheaKVCliService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRheaKVCliService.class);

    private RpcClient           rpcClient;
    private CliService          cliService;
    private CliOptions          opts;

    private boolean             started;

    @Override
    public boolean init(final CliOptions opts) {
        if (this.started) {
            LOG.info("[DefaultRheaKVRpcService] already started.");
            return true;
        }
        initCli(opts);
        LOG.info("[DefaultRheaKVCliService] start successfully, options: {}.", opts);
        return this.started = true;
    }

    @Override
    public void shutdown() {
        if (this.cliService != null) {
            this.cliService.shutdown();
        }
        this.started = false;
        LOG.info("[DefaultRheaKVCliService] shutdown successfully.");
    }

    @Override
    public Status rangeSplit(final long regionId, final long newRegionId, final String groupId, final Configuration conf) {
        final PeerId leaderId = new PeerId();
        final Status st = this.cliService.getLeader(groupId, conf, leaderId);
        if (!st.isOk()) {
            throw new IllegalStateException(st.getErrorMsg());
        }
        final RangeSplitRequest request = new RangeSplitRequest();
        request.setRegionId(regionId);
        request.setNewRegionId(newRegionId);
        try {
            final BaseResponse<?> response = (BaseResponse<?>) this.rpcClient.invokeSync(leaderId.getEndpoint(),
                request, this.opts.getTimeoutMs());
            if (response.isSuccess()) {
                return Status.OK();
            }
            return new Status(-1, "Fail to range split on region %d, error: %s", regionId, response);
        } catch (final Exception e) {
            LOG.error("Fail to range split on exception: {}.", StackTraceUtil.stackTrace(e));
            return new Status(-1, "fail to range split on region %d", regionId);
        }
    }

    private void initCli(CliOptions cliOpts) {
        if (cliOpts == null) {
            cliOpts = new CliOptions();
            cliOpts.setTimeoutMs(5000);
            cliOpts.setMaxRetry(3);
        }
        this.opts = cliOpts;
        this.cliService = RaftServiceFactory.createAndInitCliService(cliOpts);
        final CliClientService cliClientService = ((CliServiceImpl) this.cliService).getCliClientService();
        Requires.requireNonNull(cliClientService, "cliClientService");
        this.rpcClient = ((AbstractClientService) cliClientService).getRpcClient();
    }
}

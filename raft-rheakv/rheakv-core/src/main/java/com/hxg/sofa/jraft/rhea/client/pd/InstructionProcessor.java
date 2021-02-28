package com.hxg.sofa.jraft.rhea.client.pd;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.hxg.sofa.jraft.rhea.RegionEngine;
import com.hxg.sofa.jraft.rhea.StoreEngine;
import com.hxg.sofa.jraft.rhea.metadata.Instruction;
import com.hxg.sofa.jraft.rhea.metadata.Region;
import com.hxg.sofa.jraft.rhea.storage.BaseKVStoreClosure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxg.sofa.jraft.Status;
import com.hxg.sofa.jraft.rhea.util.StackTraceUtil;
import com.hxg.sofa.jraft.util.Endpoint;

/**
 * Processing the instructions from the placement driver server.
 *
 */
public class InstructionProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(InstructionProcessor.class);

    private final StoreEngine storeEngine;

    public InstructionProcessor(StoreEngine storeEngine) {
        this.storeEngine = storeEngine;
    }

    public void process(final List<Instruction> instructions) {
        LOG.info("Received instructions: {}.", instructions);
        for (final Instruction instruction : instructions) {
            if (!checkInstruction(instruction)) {
                continue;
            }
            processSplit(instruction);
            processTransferLeader(instruction);
        }
    }

    private boolean processSplit(final Instruction instruction) {
        try {
            final Instruction.RangeSplit rangeSplit = instruction.getRangeSplit();
            if (rangeSplit == null) {
                return false;
            }
            final Long newRegionId = rangeSplit.getNewRegionId();
            if (newRegionId == null) {
                LOG.error("RangeSplit#newRegionId must not be null, {}.", instruction);
                return false;
            }
            final Region region = instruction.getRegion();
            final long regionId = region.getId();
            final RegionEngine engine = this.storeEngine.getRegionEngine(regionId);
            if (engine == null) {
                LOG.error("Could not found regionEngine, {}.", instruction);
                return false;
            }
            if (!region.equals(engine.getRegion())) {
                LOG.warn("Instruction [{}] is out of date.", instruction);
                return false;
            }
            final CompletableFuture<Status> future = new CompletableFuture<>();
            this.storeEngine.applySplit(regionId, newRegionId, new BaseKVStoreClosure() {

                @Override
                public void run(Status status) {
                    future.complete(status);
                }
            });
            final Status status = future.get(20, TimeUnit.SECONDS);
            final boolean ret = status.isOk();
            if (ret) {
                LOG.info("Range-split succeeded, instruction: {}.", instruction);
            } else {
                LOG.warn("Range-split failed: {}, instruction: {}.", status, instruction);
            }
            return ret;
        } catch (final Throwable t) {
            LOG.error("Caught an exception on #processSplit: {}.", StackTraceUtil.stackTrace(t));
            return false;
        }
    }

    private boolean processTransferLeader(final Instruction instruction) {
        try {
            final Instruction.TransferLeader transferLeader = instruction.getTransferLeader();
            if (transferLeader == null) {
                return false;
            }
            final Endpoint toEndpoint = transferLeader.getMoveToEndpoint();
            if (toEndpoint == null) {
                LOG.error("TransferLeader#toEndpoint must not be null, {}.", instruction);
                return false;
            }
            final Region region = instruction.getRegion();
            final long regionId = region.getId();
            final RegionEngine engine = this.storeEngine.getRegionEngine(regionId);
            if (engine == null) {
                LOG.error("Could not found regionEngine, {}.", instruction);
                return false;
            }
            if (!region.equals(engine.getRegion())) {
                LOG.warn("Instruction [{}] is out of date.", instruction);
                return false;
            }
            return engine.transferLeadershipTo(toEndpoint);
        } catch (final Throwable t) {
            LOG.error("Caught an exception on #processTransferLeader: {}.", StackTraceUtil.stackTrace(t));
            return false;
        }
    }

    private boolean checkInstruction(final Instruction instruction) {
        if (instruction == null) {
            LOG.warn("Null instructions element.");
            return false;
        }
        if (instruction.getRegion() == null) {
            LOG.warn("Null region with instruction: {}.", instruction);
            return false;
        }
        return true;
    }
}

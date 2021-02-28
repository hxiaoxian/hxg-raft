package com.hxg.sofa.jraft.rhea.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.hxg.sofa.jraft.rhea.errors.InvalidLockAcquirerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxg.sofa.jraft.rhea.util.StackTraceUtil;
import com.hxg.sofa.jraft.util.internal.ThrowUtil;
import com.hxg.sofa.jraft.rhea.util.concurrent.DistributedLock;

/**
 * Default implementation with distributed lock.
 *
 */
class DefaultDistributedLock extends DistributedLock<byte[]> {

    private static final Logger         LOG                = LoggerFactory.getLogger(DefaultDistributedLock.class);

    private final DefaultRheaKVStore    rheaKVStore;

    private volatile ScheduledFuture<?> watchdogFuture;
    private volatile boolean            mayCancelIfRunning = false;

    protected DefaultDistributedLock(byte[] target, long lease, TimeUnit unit, ScheduledExecutorService watchdog,
                                     DefaultRheaKVStore rheaKVStore) {
        super(target, lease, unit, watchdog);
        this.rheaKVStore = rheaKVStore;
    }

    @Override
    public void unlock() {
        final byte[] internalKey = getInternalKey();
        final Acquirer acquirer = getAcquirer();
        try {
            final Owner owner = this.rheaKVStore.releaseLockWith(internalKey, acquirer).get();
            updateOwner(owner);
            if (!owner.isSameAcquirer(acquirer)) {
                final String message = String.format(
                    "an invalid acquirer [%s] trying to unlock, the real owner is [%s]", acquirer, owner);
                throw new InvalidLockAcquirerException(message);
            }
            if (owner.getAcquires() <= 0) {
                tryCancelScheduling();
            }
        } catch (final InvalidLockAcquirerException e) {
            LOG.error("Fail to unlock, {}.", StackTraceUtil.stackTrace(e));
            ThrowUtil.throwException(e);
        } catch (final Throwable t) {
            LOG.error("Fail to unlock: {}, will cancel scheduling, {}.", acquirer, StackTraceUtil.stackTrace(t));
            tryCancelScheduling();
            ThrowUtil.throwException(t);
        }
    }

    @Override
    protected Owner internalTryLock(final byte[] ctx) {
        final byte[] internalKey = getInternalKey();
        final Acquirer acquirer = getAcquirer();
        acquirer.setContext(ctx);
        final CompletableFuture<Owner> future = this.rheaKVStore.tryLockWith(internalKey, false, acquirer);
        final Owner owner = FutureHelper.get(future);
        if (!owner.isSuccess()) {
            updateOwner(owner);
            return owner;
        }

        // if success, update the fencing token in acquirer
        updateOwnerAndAcquirer(owner);

        final ScheduledExecutorService watchdog = getWatchdog();
        if (watchdog == null) {
            return owner;
        }
        // schedule keeping lease
        if (this.watchdogFuture == null) {
            synchronized (this) {
                if (this.watchdogFuture == null) {
                    final long period = (acquirer.getLeaseMillis() / 3) << 1;
                    this.watchdogFuture = scheduleKeepingLease(watchdog, internalKey, acquirer, period);
                }
            }
        }
        return owner;
    }

    private ScheduledFuture<?> scheduleKeepingLease(final ScheduledExecutorService watchdog, final byte[] key,
                                                    final Acquirer acquirer, final long period) {
        return watchdog.scheduleAtFixedRate(() -> {
            try {
                if (this.mayCancelIfRunning) {
                    // last time fail to cancel
                    tryCancelScheduling();
                    return;
                }
                this.rheaKVStore.tryLockWith(key, true, acquirer).whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        LOG.error("Fail to keeping lease with lock: {}, {}.", acquirer, StackTraceUtil.stackTrace(throwable));
                        tryCancelScheduling();
                        return;
                    }
                    if (!result.isSuccess()) {
                        LOG.warn("Fail to keeping lease with lock: {}, and result detail is: {}.", acquirer, result);
                        tryCancelScheduling();
                        return;
                    }
                    LOG.debug("Keeping lease with lock: {}.", acquirer);
                });
            } catch (final Throwable t) {
                LOG.error("Fail to keeping lease with lock: {}, {}.", acquirer, StackTraceUtil.stackTrace(t));
                tryCancelScheduling();
            }
        }, period, period, TimeUnit.MILLISECONDS);
    }

    private void tryCancelScheduling() {
        if (this.watchdogFuture != null) {
            this.mayCancelIfRunning = true;
            this.watchdogFuture.cancel(true);
        }
    }
}

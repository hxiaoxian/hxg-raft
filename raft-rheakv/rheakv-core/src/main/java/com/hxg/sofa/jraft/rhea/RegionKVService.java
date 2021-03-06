package com.hxg.sofa.jraft.rhea;

import com.hxg.sofa.jraft.rhea.cmd.store.BaseRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.BaseResponse;
import com.hxg.sofa.jraft.rhea.cmd.store.BatchDeleteRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.BatchPutRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.CompareAndPutRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.ContainsKeyRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.DeleteRangeRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.DeleteRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.GetAndPutRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.GetRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.GetSequenceRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.KeyLockRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.KeyUnlockRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.MergeRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.MultiGetRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.NodeExecuteRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.PutIfAbsentRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.PutRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.RangeSplitRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.ResetSequenceRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.ScanRequest;
import com.hxg.sofa.jraft.rhea.metadata.RegionEpoch;

public interface RegionKVService {

    long getRegionId();

    RegionEpoch getRegionEpoch();

    /**
     * {@link BaseRequest#PUT}
     */
    void handlePutRequest(final PutRequest request, final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#BATCH_PUT}
     */
    void handleBatchPutRequest(final BatchPutRequest request,
                               final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#PUT_IF_ABSENT}
     */
    void handlePutIfAbsentRequest(final PutIfAbsentRequest request,
                                  final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#GET_PUT}
     */
    void handleGetAndPutRequest(final GetAndPutRequest request,
                                final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#COMPARE_PUT}
     */
    void handleCompareAndPutRequest(final CompareAndPutRequest request,
                                    final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#DELETE}
     */
    void handleDeleteRequest(final DeleteRequest request,
                             final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#DELETE_RANGE}
     */
    void handleDeleteRangeRequest(final DeleteRangeRequest request,
                                  final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#BATCH_DELETE}
     */
    void handleBatchDeleteRequest(final BatchDeleteRequest request,
                                  final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#MERGE}
     */
    void handleMergeRequest(final MergeRequest request,
                            final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#GET}
     */
    void handleGetRequest(final GetRequest request, final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#MULTI_GET}
     */
    void handleMultiGetRequest(final MultiGetRequest request,
                               final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#CONTAINS_KEY}
     */
    void handleContainsKeyRequest(final ContainsKeyRequest request,
                                  final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#SCAN}
     */
    void handleScanRequest(final ScanRequest request, final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#GET_SEQUENCE}
     */
    void handleGetSequence(final GetSequenceRequest request,
                           final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#RESET_SEQUENCE}
     */
    void handleResetSequence(final ResetSequenceRequest request,
                             final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#KEY_LOCK}
     */
    void handleKeyLockRequest(final KeyLockRequest request,
                              final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#KEY_UNLOCK}
     */
    void handleKeyUnlockRequest(final KeyUnlockRequest request,
                                final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#NODE_EXECUTE}
     */
    void handleNodeExecuteRequest(final NodeExecuteRequest request,
                                  final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);

    /**
     * {@link BaseRequest#RANGE_SPLIT}
     */
    void handleRangeSplitRequest(final RangeSplitRequest request,
                                 final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure);
}

package com.hxg.sofa.jraft.rhea;

import java.util.concurrent.Executor;

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
import com.hxg.sofa.jraft.rhea.cmd.store.NoRegionFoundResponse;
import com.hxg.sofa.jraft.rhea.cmd.store.NodeExecuteRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.PutIfAbsentRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.PutRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.RangeSplitRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.ResetSequenceRequest;
import com.hxg.sofa.jraft.rhea.cmd.store.ScanRequest;
import com.hxg.sofa.jraft.rhea.errors.Errors;
import com.hxg.sofa.jraft.rhea.errors.RheaRuntimeException;
import com.hxg.sofa.jraft.rpc.RpcContext;
import com.hxg.sofa.jraft.rpc.RpcProcessor;
import com.hxg.sofa.jraft.util.Requires;

/**
 * Rhea KV store RPC request processing service.
 *
 */
public class KVCommandProcessor<T extends BaseRequest> implements RpcProcessor<T> {

    private final Class<T>    reqClazz;
    private final StoreEngine storeEngine;

    public KVCommandProcessor(Class<T> reqClazz, StoreEngine storeEngine) {
        this.reqClazz = Requires.requireNonNull(reqClazz, "reqClazz");
        this.storeEngine = Requires.requireNonNull(storeEngine, "storeEngine");
    }

    @Override
    public void handleRequest(final RpcContext rpcCtx, final T request) {
        Requires.requireNonNull(request, "request");
        final RequestProcessClosure<BaseRequest, BaseResponse<?>> closure = new RequestProcessClosure<>(request, rpcCtx);
        final RegionKVService regionKVService = this.storeEngine.getRegionKVService(request.getRegionId());
        if (regionKVService == null) {
            final NoRegionFoundResponse noRegion = new NoRegionFoundResponse();
            noRegion.setRegionId(request.getRegionId());
            noRegion.setError(Errors.NO_REGION_FOUND);
            noRegion.setValue(false);
            closure.sendResponse(noRegion);
            return;
        }
        switch (request.magic()) {
            case BaseRequest.PUT:
                regionKVService.handlePutRequest((PutRequest) request, closure);
                break;
            case BaseRequest.BATCH_PUT:
                regionKVService.handleBatchPutRequest((BatchPutRequest) request, closure);
                break;
            case BaseRequest.PUT_IF_ABSENT:
                regionKVService.handlePutIfAbsentRequest((PutIfAbsentRequest) request, closure);
                break;
            case BaseRequest.GET_PUT:
                regionKVService.handleGetAndPutRequest((GetAndPutRequest) request, closure);
                break;
            case BaseRequest.COMPARE_PUT:
                regionKVService.handleCompareAndPutRequest((CompareAndPutRequest) request, closure);
                break;
            case BaseRequest.DELETE:
                regionKVService.handleDeleteRequest((DeleteRequest) request, closure);
                break;
            case BaseRequest.DELETE_RANGE:
                regionKVService.handleDeleteRangeRequest((DeleteRangeRequest) request, closure);
                break;
            case BaseRequest.BATCH_DELETE:
                regionKVService.handleBatchDeleteRequest((BatchDeleteRequest) request, closure);
                break;
            case BaseRequest.MERGE:
                regionKVService.handleMergeRequest((MergeRequest) request, closure);
                break;
            case BaseRequest.GET:
                regionKVService.handleGetRequest((GetRequest) request, closure);
                break;
            case BaseRequest.MULTI_GET:
                regionKVService.handleMultiGetRequest((MultiGetRequest) request, closure);
                break;
            case BaseRequest.CONTAINS_KEY:
                regionKVService.handleContainsKeyRequest((ContainsKeyRequest) request, closure);
                break;
            case BaseRequest.SCAN:
                regionKVService.handleScanRequest((ScanRequest) request, closure);
                break;
            case BaseRequest.GET_SEQUENCE:
                regionKVService.handleGetSequence((GetSequenceRequest) request, closure);
                break;
            case BaseRequest.RESET_SEQUENCE:
                regionKVService.handleResetSequence((ResetSequenceRequest) request, closure);
                break;
            case BaseRequest.KEY_LOCK:
                regionKVService.handleKeyLockRequest((KeyLockRequest) request, closure);
                break;
            case BaseRequest.KEY_UNLOCK:
                regionKVService.handleKeyUnlockRequest((KeyUnlockRequest) request, closure);
                break;
            case BaseRequest.NODE_EXECUTE:
                regionKVService.handleNodeExecuteRequest((NodeExecuteRequest) request, closure);
                break;
            case BaseRequest.RANGE_SPLIT:
                regionKVService.handleRangeSplitRequest((RangeSplitRequest) request, closure);
                break;
            default:
                throw new RheaRuntimeException("Unsupported request type: " + request.getClass().getName());
        }
    }

    @Override
    public String interest() {
        return this.reqClazz.getName();
    }

    @Override
    public Executor executor() {
        return this.storeEngine.getKvRpcExecutor();
    }
}

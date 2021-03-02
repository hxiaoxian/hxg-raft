package com.hxg.sofa.jraft.rpc;

import java.util.concurrent.Executor;

import com.hxg.sofa.jraft.rpc.impl.PingRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.cli.AddLearnersRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.cli.AddPeerRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.cli.ChangePeersRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.cli.GetLeaderRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.cli.GetPeersRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.cli.RemoveLearnersRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.cli.RemovePeerRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.cli.ResetLearnersRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.cli.ResetPeerRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.cli.SnapshotRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.cli.TransferLeaderRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.core.AppendEntriesRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.core.GetFileRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.core.InstallSnapshotRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.core.ReadIndexRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.core.RequestVoteRequestProcessor;
import com.hxg.sofa.jraft.rpc.impl.core.TimeoutNowRequestProcessor;
import com.hxg.sofa.jraft.util.Endpoint;
import com.hxg.sofa.jraft.util.RpcFactoryHelper;

/**
 * Raft RPC server factory.
 *
 *     
 *
 */
public class RaftRpcServerFactory {

    static {
        ProtobufMsgFactory.load();
    }

    /**
     * Creates a raft RPC server with default request executors.
     *
     * @param endpoint server address to bind
     * @return a rpc server instance
     */
    public static RpcServer createRaftRpcServer(final Endpoint endpoint) {
        return createRaftRpcServer(endpoint, null, null);
    }

    /**
     * Creates a raft RPC server with executors to handle requests.
     *
     * @param endpoint      server address to bind
     * @param raftExecutor  executor to handle RAFT requests.
     * @param cliExecutor   executor to handle CLI service requests.
     * @return a rpc server instance
     */
    public static RpcServer createRaftRpcServer(final Endpoint endpoint, final Executor raftExecutor,
                                                final Executor cliExecutor) {
        final RpcServer rpcServer = RpcFactoryHelper.rpcFactory().createRpcServer(endpoint);
        addRaftRequestProcessors(rpcServer, raftExecutor, cliExecutor);
        return rpcServer;
    }

    /**
     * Adds RAFT and CLI service request processors with default executor.
     *
     * @param rpcServer rpc server instance
     */
    public static void addRaftRequestProcessors(final RpcServer rpcServer) {
        addRaftRequestProcessors(rpcServer, null, null);
    }

    /**
     * Adds RAFT and CLI service request processors.
     *
     * @param rpcServer    rpc server instance
     * @param raftExecutor executor to handle RAFT requests.
     * @param cliExecutor  executor to handle CLI service requests.
     */
    public static void addRaftRequestProcessors(final RpcServer rpcServer, final Executor raftExecutor,
                                                final Executor cliExecutor) {
        // raft core processors
        final AppendEntriesRequestProcessor appendEntriesRequestProcessor = new AppendEntriesRequestProcessor(
            raftExecutor);
        rpcServer.registerConnectionClosedEventListener(appendEntriesRequestProcessor);
        rpcServer.registerProcessor(appendEntriesRequestProcessor);
        rpcServer.registerProcessor(new GetFileRequestProcessor(raftExecutor));
        rpcServer.registerProcessor(new InstallSnapshotRequestProcessor(raftExecutor));
        rpcServer.registerProcessor(new RequestVoteRequestProcessor(raftExecutor));
        rpcServer.registerProcessor(new PingRequestProcessor());
        rpcServer.registerProcessor(new TimeoutNowRequestProcessor(raftExecutor));
        rpcServer.registerProcessor(new ReadIndexRequestProcessor(raftExecutor));
        // raft cli service
        rpcServer.registerProcessor(new AddPeerRequestProcessor(cliExecutor));
        rpcServer.registerProcessor(new RemovePeerRequestProcessor(cliExecutor));
        rpcServer.registerProcessor(new ResetPeerRequestProcessor(cliExecutor));
        rpcServer.registerProcessor(new ChangePeersRequestProcessor(cliExecutor));
        rpcServer.registerProcessor(new GetLeaderRequestProcessor(cliExecutor));
        rpcServer.registerProcessor(new SnapshotRequestProcessor(cliExecutor));
        rpcServer.registerProcessor(new TransferLeaderRequestProcessor(cliExecutor));
        rpcServer.registerProcessor(new GetPeersRequestProcessor(cliExecutor));
        rpcServer.registerProcessor(new AddLearnersRequestProcessor(cliExecutor));
        rpcServer.registerProcessor(new RemoveLearnersRequestProcessor(cliExecutor));
        rpcServer.registerProcessor(new ResetLearnersRequestProcessor(cliExecutor));
    }

    /**
     * Creates a raft RPC server and starts it.
     *
     * @param endpoint server address to bind
     * @return a rpc server instance
     */
    public static RpcServer createAndStartRaftRpcServer(final Endpoint endpoint) {
        return createAndStartRaftRpcServer(endpoint, null, null);
    }

    /**
     * Creates a raft RPC server and starts it.
     *
     * @param endpoint     server address to bind
     * @param raftExecutor executor to handle RAFT requests.
     * @param cliExecutor  executor to handle CLI service requests.
     * @return a rpc server instance
     */
    public static RpcServer createAndStartRaftRpcServer(final Endpoint endpoint, final Executor raftExecutor,
                                                        final Executor cliExecutor) {
        final RpcServer server = createRaftRpcServer(endpoint, raftExecutor, cliExecutor);
        server.init(null);
        return server;
    }
}

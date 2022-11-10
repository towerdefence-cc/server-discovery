package cc.towerdefence.api.serverdiscovery.controller;

import cc.towerdefence.api.service.ServerDiscoveryGrpc;
import cc.towerdefence.api.service.ServerDiscoveryProto;
import cc.towerdefence.api.utils.utils.FunctionalFutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import dev.agones.allocation.AllocationProto;
import dev.agones.allocation.AllocationServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ForkJoinPool;


@Controller
@GrpcService
@RequiredArgsConstructor
public class ServerOrchestratorController extends ServerDiscoveryGrpc.ServerDiscoveryImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerOrchestratorController.class);

    private final AllocationServiceGrpc.AllocationServiceFutureStub allocationService;

    @Override
    public void getSuggestedLobbyServer(Empty request, StreamObserver<ServerDiscoveryProto.LobbyServer> responseObserver) {
        ListenableFuture<AllocationProto.AllocationResponse> allocationFuture = this.allocationService.allocate(AllocationProto.AllocationRequest.newBuilder()
                .setNamespace("towerdefence")
                .addGameServerSelectors(
                        AllocationProto.GameServerSelector.newBuilder()
                                .putMatchLabels("agones.dev/fleet", "lobby")
                                .setGameServerState(AllocationProto.GameServerSelector.GameServerState.ALLOCATED)
                                .setPlayers(
                                        AllocationProto.PlayerSelector.newBuilder()
                                                .setMinAvailable(1)
                                                .setMaxAvailable(Integer.MAX_VALUE)
                                                .build()
                                )
                ).addGameServerSelectors(
                        AllocationProto.GameServerSelector.newBuilder()
                                .putMatchLabels("agones.dev/fleet", "lobby")
                                .setGameServerState(AllocationProto.GameServerSelector.GameServerState.READY)
                )
                .setScheduling(AllocationProto.AllocationRequest.SchedulingStrategy.Packed)
                .build());

        Futures.addCallback(allocationFuture, FunctionalFutureCallback.create(
                allocation -> {
                    responseObserver.onNext(ServerDiscoveryProto.LobbyServer.newBuilder()
                            .setConnectableServer(
                                    ServerDiscoveryProto.ConnectableServer.newBuilder()
                                            .setAddress(allocation.getAddress())
                                            .setPort(allocation.getPortsList().get(0).getPort())
                                            .setId(allocation.getGameServerName())
                                            .build()
                            ).build());
                },
                throwable -> {
                    LOGGER.error("Error getting LOBBY server: ", throwable);
                    responseObserver.onError(throwable);
                }
        ), ForkJoinPool.commonPool());
    }

    @Override
    public void getSuggestedOtpServer(Empty request, StreamObserver<ServerDiscoveryProto.ConnectableServer> responseObserver) {
        ListenableFuture<AllocationProto.AllocationResponse> allocationFuture = this.allocationService.allocate(AllocationProto.AllocationRequest.newBuilder()
                .setNamespace("towerdefence")
                .addGameServerSelectors(
                        AllocationProto.GameServerSelector.newBuilder()
                                .putMatchLabels("agones.dev/fleet", "void-otp")
                                .setGameServerState(AllocationProto.GameServerSelector.GameServerState.ALLOCATED)
                                .setPlayers(
                                        AllocationProto.PlayerSelector.newBuilder()
                                                .setMinAvailable(1)
                                                .setMaxAvailable(Integer.MAX_VALUE)
                                                .build()
                                )
                ).addGameServerSelectors(
                        AllocationProto.GameServerSelector.newBuilder()
                                .putMatchLabels("agones.dev/fleet", "void-otp")
                                .setGameServerState(AllocationProto.GameServerSelector.GameServerState.READY)
                )
                .setScheduling(AllocationProto.AllocationRequest.SchedulingStrategy.Packed)
                .build());

        Futures.addCallback(allocationFuture, FunctionalFutureCallback.create(
                allocation -> {
                    responseObserver.onNext(ServerDiscoveryProto.ConnectableServer.newBuilder()
                            .setAddress(allocation.getAddress())
                            .setPort(allocation.getPortsList().get(0).getPort())
                            .setId(allocation.getGameServerName())
                            .build()
                    );
                },
                throwable -> {
                    LOGGER.error("Error getting VOID-OTP server: ", throwable);
                    responseObserver.onError(throwable);
                }
        ), ForkJoinPool.commonPool());
    }
}

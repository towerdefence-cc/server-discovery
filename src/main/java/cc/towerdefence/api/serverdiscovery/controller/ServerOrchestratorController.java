package cc.towerdefence.api.serverdiscovery.controller;

import cc.towerdefence.api.model.server.ConnectableServerProto;
import cc.towerdefence.api.model.server.LobbyServerProto;
import cc.towerdefence.api.service.ServerDiscoveryGrpc;
import com.google.protobuf.Empty;
import dev.agones.allocation.AllocationProto;
import dev.agones.allocation.AllocationServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;


@Controller
@GrpcService
@RequiredArgsConstructor
public class ServerOrchestratorController extends ServerDiscoveryGrpc.ServerDiscoveryImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerOrchestratorController.class);

    private final AllocationServiceGrpc.AllocationServiceStub allocationServiceStub;

    @Override
    public void getSuggestedLobbyServer(Empty request, StreamObserver<LobbyServerProto.LobbyServer> responseObserver) {
        this.allocationServiceStub.allocate(AllocationProto.AllocationRequest.newBuilder()
                .setNamespace("towerdefence")
                .addGameServerSelectors(
                        AllocationProto.GameServerSelector.newBuilder()
                                .putMatchLabels("agones.dev/fleet", "lobby")
                                .setGameServerState(AllocationProto.GameServerSelector.GameServerState.ALLOCATED)
                ).addGameServerSelectors(
                        AllocationProto.GameServerSelector.newBuilder()
                                .putMatchLabels("agones.dev/fleet", "lobby")
                                .setGameServerState(AllocationProto.GameServerSelector.GameServerState.READY)
                )
                .setScheduling(AllocationProto.AllocationRequest.SchedulingStrategy.Packed)
                .build(), new StreamObserver<>() {
            @Override
            public void onNext(AllocationProto.AllocationResponse value) {
                responseObserver.onNext(LobbyServerProto.LobbyServer.newBuilder()
                        .setConnectableServer(
                                ConnectableServerProto.ConnectableServer.newBuilder()
                                        .setAddress(value.getAddress())
                                        .setPort(value.getPortsList().get(0).getPort())
                                        .setId(value.getGameServerName())
                                        .build()
                        ).build());
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.error("Error getting LOBBY server: ", throwable);
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        });
    }
}

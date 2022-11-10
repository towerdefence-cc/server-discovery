package cc.towerdefence.api.serverdiscovery.config;

import dev.agones.allocation.AllocationServiceGrpc;
import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgonesConfig {

    @Bean
    public ManagedChannel managedChannel(ChannelCredentials channelCredentials) {
        String host = System.getenv("AGONES_ALLOCATOR_ADDRESS");
        int port = Integer.parseInt(System.getenv("AGONES_ALLOCATOR_PORT"));

        return Grpc.newChannelBuilderForAddress(host, port, channelCredentials)
                .defaultLoadBalancingPolicy("round_robin")
                .build();
    }

    @Bean
    public AllocationServiceGrpc.AllocationServiceFutureStub allocationServiceStub(ManagedChannel managedChannel) {
        return AllocationServiceGrpc.newFutureStub(managedChannel);
    }
}

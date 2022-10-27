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
        return Grpc.newChannelBuilderForAddress("agones-allocator.agones-system.svc", 10000, channelCredentials)
                .build();
    }

    @Bean
    public AllocationServiceGrpc.AllocationServiceStub allocationServiceStub(ManagedChannel managedChannel) {
        return AllocationServiceGrpc.newStub(managedChannel);
    }
}

package cc.towerdefence.api.serverorchestratorjava.config.agones;

import io.grpc.ChannelCredentials;
import io.grpc.TlsChannelCredentials;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;
import java.security.Security;

@Configuration
@Profile("development")
public class AgonesDevKeyConfig {
    private static final Path AGONES_KEY_PATH = Path.of("keys/client.key");
    private static final Path AGONES_CERTIFICATE_PATH = Path.of("keys/client.crt");

    @Value("classpath:ca.crt")
    private Resource caCertificate;

    @Bean
    public ChannelCredentials sslContext() throws IOException {
        return TlsChannelCredentials.newBuilder()
                .keyManager(AGONES_CERTIFICATE_PATH.toFile(), AGONES_KEY_PATH.toFile())
                .trustManager(this.caCertificate.getFile())
                .build();
    }

    @PostConstruct
    private void initialize() {
        Security.addProvider(new BouncyCastleProvider());
    }
}

package cc.towerdefence.api.serverdiscovery.config.agones;

import io.grpc.ChannelCredentials;
import io.grpc.TlsChannelCredentials;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;
import java.security.Security;

@Configuration
@Profile("production")
public class AgonesProdKeyConfig {
    private static final Path AGONES_KEY_PATH = Path.of("/secrets/keys/tls.key");
    private static final Path AGONES_CERTIFICATE_PATH = Path.of("/secrets/keys/tls.crt");
    private static final Path AGONES_CA_CERTIFICATE_PATH = Path.of("/secrets/keys/ca.crt");

    @Bean
    public ChannelCredentials sslContext() throws IOException {
        return TlsChannelCredentials.newBuilder()
                .keyManager(AGONES_CERTIFICATE_PATH.toFile(), AGONES_KEY_PATH.toFile())
                .trustManager(AGONES_CA_CERTIFICATE_PATH.toFile())
                .build();
    }

    @PostConstruct
    private void initialize() {
        Security.addProvider(new BouncyCastleProvider());
    }
}

package webapp.exchangerates.configuration.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityJwtConfiguration {

    @Value("${jwt.secretKey}")
    private String jwtSecret;

    @Bean
    JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8))));
    }
}

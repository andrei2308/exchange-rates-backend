package webapp.exchangerates.configuration.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class WebSecurity {

    @Value("${USERNAME}")
    private String ADMIN_USERNAME;

    @Value("${PASSWORD}")
    private String ADMIN_PASSWORD;

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/exchange-rate").hasRole("ADMIN")  // Use hasRole instead
                        .requestMatchers("/api/exchange/**").hasRole("ADMIN")
                        .requestMatchers("/rate").authenticated()
                        .requestMatchers("/api/v1/stripe/**").hasRole("ADMIN")
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions().sameOrigin()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = jwtSecretKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA512");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec).build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails adminUser = User.builder()
                .username(ADMIN_USERNAME)
                .password(passwordEncoder().encode(ADMIN_PASSWORD))
                .roles("ADMIN", "USER")
                .build();

        return new InMemoryUserDetailsManager(adminUser);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("roles");
        converter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(converter);
        return authenticationConverter;
    }
}
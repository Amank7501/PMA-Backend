package com.rasp.app;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import java.time.Instant;
import java.util.Map;

@Configuration
public class TestConfig {
    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate() {
        return Mockito.mock(JdbcTemplate.class);


    }

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        return token -> new Jwt(
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of(
                        "sub", "test-user",
                        "scope", "openid profile email",
                        "preferred_username", "testuser"
                )
        );
    }

}
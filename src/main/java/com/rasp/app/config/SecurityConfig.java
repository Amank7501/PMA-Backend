package com.rasp.app.config;

//import com.example.demo.service.RoleResourceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;
    private final JwtDecoder jwtDecoder;
    private final JdbcTemplate jdbcTemplate;
    private final KeycloakTokenFilter KeycloakTokenFilter;
    private final RoleFilter roleFilter;



   /// private final RoleResourceService roleResourceService;

    public SecurityConfig(JwtAuthConverter jwtAuthConverter, JwtDecoder jwtDecoder, JdbcTemplate jdbcTemplate, com.rasp.app.config.KeycloakTokenFilter keycloakTokenFilter, RoleFilter roleFilter) {
        this.jwtAuthConverter = jwtAuthConverter;
        this.jwtDecoder = jwtDecoder;
        this.jdbcTemplate = jdbcTemplate;
        //this.roleResourceService = roleResourceService;
        this. KeycloakTokenFilter = keycloakTokenFilter;
        this.roleFilter = roleFilter;
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsFilter()))
                .csrf(AbstractHttpConfigurer::disable)
                //.csrf(csrf -> csrf.ignoringRequestMatchers("/api/auth/**"))

//                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register","/api/auth/login", "/api/auth/callback", "/api/auth/logout","/api/auth/addUser","/api/v1/role_resource","/api/auth/add-client-role","/api/auth/assign-client-role","/api/auth/user_resource_role","/api/role_resource_permission","/api/auth/add_user","/api/generateApp","/api/resource_role","/api/auth/user_role_mapping","/api/getAllResourceMetaData","/api/GetAllResource","/api/getAllResourceMetaData/{resource}","/api/auth/role","/api/auth/{roleName}/users","/api/role_user_res_instance","/api/auth/users-with-roles","/api/auth/update-user/{userId}").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(KeycloakTokenFilter ,
                        BearerTokenAuthenticationFilter.class) // Add custom introspection filter
                .addFilterAfter(roleFilter, BearerTokenAuthenticationFilter.class)
               .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)));

        return http.build();
    }

    @Bean
        public CorsConfigurationSource corsFilter() {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true); // Allow credentials (cookies)
            config.setAllowedOrigins(Arrays.asList("http://localhost:3001")); // Explicitly allow frontend
            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
            config.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie")); // Ensure cookies are exposed

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);
            return source;
        }
}

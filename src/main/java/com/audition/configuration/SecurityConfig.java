package com.audition.configuration;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class for setting up HTTP security.
 *
 * <p>This configuration ensures that sensitive actuator endpoints are protected and only accessible
 * to users with the appropriate role, while allowing public access to basic health and info endpoints.
 * It also enforces authentication for all other requests using HTTP Basic Authentication.</p>
 *
 * <ul>
 *   <li><strong>"info" and "health" endpoints</strong>: Accessible to everyone without authentication.</li>
 *   <li><strong>All other actuator endpoints</strong>: Require the user to have the "ACTUATOR_ADMIN" role.</li>
 *   <li><strong>All other requests</strong>: Require the user to be authenticated.</li>
 *   <li><strong>HTTP Basic Authentication</strong>: Used for authentication.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests
                .requestMatchers(EndpointRequest.to("info", "health")).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR_ADMIN")
                .anyRequest().authenticated())

            .httpBasic();

        return http.build();
    }

}

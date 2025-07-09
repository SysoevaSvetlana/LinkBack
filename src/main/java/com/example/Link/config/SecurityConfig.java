package com.example.Link.config;

import com.example.Link.converter.KeycloakRoleConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

// SecurityConfig.java
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final KeycloakRoleConverter converter;

    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**","/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/links").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.PATCH,"/links/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/links/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/r/**").permitAll()   // редирект
                        .anyRequest().authenticated())
                .oauth2ResourceServer(o -> o.jwt(j -> j
                        .jwtAuthenticationConverter(src -> {
                            var c = new JwtAuthenticationConverter();
                            c.setJwtGrantedAuthoritiesConverter(converter);
                            return c.convert(src);
                        })));
        return http.build();
    }
}


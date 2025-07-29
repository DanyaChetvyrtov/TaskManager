package ru.zuzex.practice.taskms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.zuzex.practice.taskms.security.jwt.JwtTokenFilter;
import ru.zuzex.practice.taskms.security.jwt.JwtTokenUtils;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Lazy
    private final JwtTokenUtils jwtTokenUtils;

    public SecurityConfig(@Lazy JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
//                .exceptionHandling(configurer -> configurer
//                        .authenticationEntryPoint(
//                                (request, response, exception) -> {
//                                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                                    response.getWriter().write("Unauthorized.");
//                                }
//                        )
//                        .accessDeniedHandler(
//                                (request, response, exception) -> {
//                                    response.setStatus(HttpStatus.FORBIDDEN.value());
//                                    response.getWriter().write("Forbidden.");
//                                }
//                        )
//                )
                .authorizeHttpRequests(configurer -> configurer
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/task/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtTokenFilter(jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
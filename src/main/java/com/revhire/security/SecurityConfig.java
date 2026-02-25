package com.revhire.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final UserDetailsService auraUserDetailsService;

        public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                        UserDetailsService auraUserDetailsService) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.auraUserDetailsService = auraUserDetailsService;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> {
                                }) // rely on CorsConfig Bean
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(authz -> authz
                                                // Public endpoints
                                                .requestMatchers("/api/auth/**", "/api/v1/auth/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/jobs/**", "/api/v1/jobs/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/resume/view/**",
                                                                "/api/v1/resume/view/**")
                                                .permitAll()
                                                // Frontend and assets (if any)
                                                .requestMatchers("/", "/index.html", "/favicon.ico", "/*.js",
                                                                "/*.css", "/assets/**", "/*.png", "/*.jpg")
                                                .permitAll()
                                                // Swagger
                                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()

                                                // Admin endpoints
                                                .requestMatchers("/api/admin/**", "/api/v1/admin/**").hasRole("ADMIN")

                                                // Employer endpoints
                                                .requestMatchers(HttpMethod.POST, "/api/jobs/**", "/api/v1/jobs/**")
                                                .hasRole("EMPLOYER")
                                                .requestMatchers(HttpMethod.PUT, "/api/jobs/**", "/api/v1/jobs/**")
                                                .hasRole("EMPLOYER")
                                                .requestMatchers(HttpMethod.DELETE, "/api/jobs/**", "/api/v1/jobs/**")
                                                .hasRole("EMPLOYER")
                                                .requestMatchers("/api/employer/**", "/api/v1/employer/**")
                                                .hasRole("EMPLOYER")

                                                // Job Seeker endpoints
                                                .requestMatchers("/api/applications/**", "/api/v1/applications/**")
                                                .hasAnyRole("JOB_SEEKER", "EMPLOYER")
                                                .requestMatchers("/api/saved-jobs/**", "/api/v1/saved-jobs/**")
                                                .hasRole("JOB_SEEKER")
                                                .requestMatchers("/api/seeker/**", "/api/v1/seeker/**")
                                                .hasRole("JOB_SEEKER")
                                                .requestMatchers("/api/resume/**", "/api/v1/resume/**")
                                                .hasAnyRole("JOB_SEEKER", "EMPLOYER", "ADMIN")
                                                .requestMatchers("/api/notifications/**", "/api/v1/notifications/**")
                                                .hasAnyRole("JOB_SEEKER", "EMPLOYER")

                                                .anyRequest().authenticated());

                http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(10);
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(auraUserDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }
}

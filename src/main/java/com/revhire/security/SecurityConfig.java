package com.revhire.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Spring Security 6 Configuration for RevHire Application
 * 
 * Key Features:
 * - Email-based authentication (not username)
 * - BCrypt password encoding
 * - Role-based access control (SEEKER, EMPLOYER, ADMIN)
 * - Custom success handler for role-based redirects
 * - CSRF protection enabled
 * - Session management
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

        private final UserDetailsService userDetailsService;
        private final AuthenticationSuccessHandler successHandler;

        public SecurityConfig(UserDetailsService userDetailsService,
                        AuthenticationSuccessHandler successHandler) {
                this.userDetailsService = userDetailsService;
                this.successHandler = successHandler;
        }

        /**
         * Main Security Filter Chain - Spring Boot 3 / Security 6 approach
         * NO WebSecurityConfigurerAdapter needed
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // Authorization rules
                                .authorizeHttpRequests(authz -> authz
                                                // Public endpoints - accessible without authentication
                                                .requestMatchers(
                                                                "/",
                                                                "/home",
                                                                "/register",
                                                                "/login",
                                                                "/error",
                                                                "/jobs/**", // Public viewing
                                                                "/employers/**", // Public viewing
                                                                "/css/**",
                                                                "/js/**",
                                                                "/images/**",
                                                                "/webjars/**",
                                                                "/favicon.ico")
                                                .permitAll()

                                                // ADMIN endpoints - requires ADMIN role
                                                .requestMatchers("/admin/**").hasRole("ADMIN")

                                                // Employer-specific endpoints - requires EMPLOYER role
                                                .requestMatchers("/employer/**").hasRole("EMPLOYER")

                                                // Job Seeker-specific endpoints - requires SEEKER role
                                                .requestMatchers("/seeker/**", "/resume/**").hasRole("JOB_SEEKER")

                                                // Application endpoints - requires SEEKER role
                                                .requestMatchers("/applications/**").hasRole("JOB_SEEKER")

                                                // Job browsing - publicly accessible now

                                                // All other requests require authentication
                                                .anyRequest().authenticated())

                                // Form Login Configuration
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login") // POST endpoint for login

                                                // CRITICAL FIX: Use 'username' parameter for email field
                                                .usernameParameter("username") // Changed from default to match our form
                                                .passwordParameter("password") // Explicitly set (though this is
                                                                               // default)

                                                .successHandler(successHandler) // Custom success handler for role-based
                                                                                // redirect
                                                .failureUrl("/login?error=true") // Redirect on authentication failure
                                                .permitAll())

                                // Logout Configuration
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout=true")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())

                                // Exception Handling
                                .exceptionHandling(ex -> ex
                                                .accessDeniedPage("/error?code=403"))

                                // Session Management
                                .sessionManagement(session -> session
                                                .maximumSessions(1) // Only one session per user
                                                .maxSessionsPreventsLogin(false) // New login invalidates old session
                                )

                                // Remember Me Configuration
                                .rememberMe(remember -> remember
                                                .key("uniqueAndSecretReviewHireKey")
                                                .tokenValiditySeconds(86400) // 1 day
                                                .userDetailsService(userDetailsService));

                return http.build();
        }

        /**
         * Password Encoder Bean - BCrypt with strength 10
         * MUST be defined as a bean for Spring Security to use it
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(10); // Strength 10 is good balance of security/performance
        }

        /**
         * Authentication Provider - connects UserDetailsService with PasswordEncoder
         * This is critical for email-based authentication
         */
        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        /**
         * Authentication Manager - required for programmatic authentication
         */
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }
}

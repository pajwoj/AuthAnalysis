package pl.pajwoj.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import pl.pajwoj.jwt.JWTAuthenticationFilter;
import pl.pajwoj.jwt.JWTTokenProvider;
import pl.pajwoj.responses.APIResponse;

@ConditionalOnProperty(name = "auth.type", havingValue = "jwt")
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class JWTSecurityConfig {
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final JWTTokenProvider jwtTokenProvider;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login", "/api/csrf", "/api/config", "/api/logout").permitAll()
                        .requestMatchers("/api/user").authenticated()
                        .requestMatchers("/api/protected").hasAuthority("SECRET")
                        .anyRequest().denyAll())

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(APIResponse.json("You are not logged in"));
                        })

                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write(APIResponse.json("Only admins can access this page"));
                        }))

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .formLogin(AbstractHttpConfigurer::disable)

                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            String token = request.getHeader("Authorization").substring(7);
                            jwtTokenProvider.blacklistToken(token);
                        })

                        .logoutSuccessHandler((request, response, auth) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json");
                            response.getWriter().write(APIResponse.json("Logout successful"));
                        })
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }
}
package pl.pajwoj.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfigurationSource;
import pl.pajwoj.responses.APIResponse;

@ConditionalOnProperty(name = "auth.type", havingValue = "session")
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SessionSecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        .ignoringRequestMatchers("/api/csrf"))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/login", "/api/logout", "/api/user", "/api/csrf", "/api/config").permitAll()
                        .requestMatchers("/api/protected").hasAuthority("SECRET")
                        .anyRequest().authenticated())

                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .expiredSessionStrategy((event) -> {
                            val response = event.getResponse();

                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(APIResponse.jsonString("SESSION_EXPIRED", "Session expired! Log in again. Redirecting to homepage..."));
                        })
                )

                .formLogin(AbstractHttpConfigurer::disable)

                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler((request, response, auth) -> {
                            response.setHeader("Clear-Site-Data", "\"cookies\"");
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json");
                            response.getWriter().write(APIResponse.jsonString("", "Logout successful"));
                        })
                )
        ;

        return http.build();
    }
}

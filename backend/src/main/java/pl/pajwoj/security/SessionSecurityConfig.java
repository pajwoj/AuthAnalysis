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
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.cors.CorsConfigurationSource;
import pl.pajwoj.responses.APIResponse;

@ConditionalOnProperty(name = "auth.type", havingValue = "session")
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableSpringHttpSession
public class SessionSecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();

        cookieSerializer.setCookieName("SESSIONID");
        cookieSerializer.setCookiePath("/");
        cookieSerializer.setUseHttpOnlyCookie(true);
        cookieSerializer.setUseSecureCookie(true);
        cookieSerializer.setSameSite("Strict");
        cookieSerializer.setCookieMaxAge(60 * 15);

        return cookieSerializer;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        .ignoringRequestMatchers("/api/csrf"))

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
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .sessionRegistry(null)
                        .expiredSessionStrategy((event) -> {
                            val response = event.getResponse();

                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(APIResponse.json("Session expired, log in again."));
                        })
                )

                .formLogin(AbstractHttpConfigurer::disable)

                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("SESSIONID")
                        .logoutSuccessHandler((request, response, auth) -> {
                            response.setHeader("Clear-Site-Data", "\"cookies\"");
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json");
                            response.getWriter().write(APIResponse.json("Logout successful"));
                        })
                )
        ;

        return http.build();
    }
}

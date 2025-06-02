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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.cors.CorsConfigurationSource;
import pl.pajwoj.responses.APIResponse;
import pl.pajwoj.services.OAuth2UserServiceImpl;

@ConditionalOnProperty(name = "auth.type", havingValue = "oauth")
@Configuration
@EnableWebSecurity
@EnableSpringHttpSession
@RequiredArgsConstructor
public class OAuthSecurityConfig {

    private final OAuth2UserServiceImpl oauth2UserService;
    private final OidcUserService oidcUserService;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();

        cookieSerializer.setCookieName("SESSIONID");
        cookieSerializer.setCookiePath("/");
        cookieSerializer.setUseHttpOnlyCookie(true);
        cookieSerializer.setUseSecureCookie(true);
        cookieSerializer.setSameSite("Lax");
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
                        .ignoringRequestMatchers("/api/csrf", "/oauth2/authorization/**", "/login/oauth2/code/**"))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login", "/api/csrf", "/api/config", "/api/logout", "/oauth2/authorization/**", "/login/oauth2/code/**").permitAll()
                        .requestMatchers("/api/user").authenticated()
                        .requestMatchers("/api/protected").hasAuthority("SECRET")
                        .anyRequest().denyAll())

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, accessDeniedException) -> {
                            System.out.println(accessDeniedException.toString());
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(APIResponse.json("You are not logged in"));
                        })

                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write(APIResponse.json("Only admins can access this page"));
                        }))

                .formLogin(AbstractHttpConfigurer::disable)

                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(oidcUserService)
                                .userService(oauth2UserService)
                        )

                        .successHandler((request, response, authentication) -> {
                            String userAgent = request.getHeader("User-Agent");

                            if (userAgent != null)
                                request.getSession().setAttribute("USER_AGENT", userAgent);

                            response.sendRedirect("http://localhost:5173");
                        })

                        .failureHandler((request, response, exception) -> {
                            response.sendRedirect("http://localhost:5173");
                        })
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
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
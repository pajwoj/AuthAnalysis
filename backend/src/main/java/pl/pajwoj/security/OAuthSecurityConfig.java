package pl.pajwoj.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.cors.CorsConfigurationSource;
import pl.pajwoj.responses.APIResponse;
import pl.pajwoj.services.OAuth2UserServiceImpl;

@ConditionalOnProperty(name = "auth.type", havingValue = "oauth")
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class OAuthSecurityConfig {

    private final OAuth2UserServiceImpl oauth2UserService;
    private final OidcUserService oidcUserService;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user", "/api/config", "/api/logout", "/api/protected", "/login/oauth2/code/**", "/oauth2/authorization/**").permitAll()
                        .anyRequest().authenticated())

                .formLogin(AbstractHttpConfigurer::disable)

                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(oidcUserService)
                                .userService(oauth2UserService)
                        )
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect("http://localhost:5173/");
                        })
                        .failureHandler((request, response, exception) -> {
                            System.out.println("fail!");
                            response.sendRedirect("http://localhost:5173/");
                        })
                )

                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .addLogoutHandler(new SecurityContextLogoutHandler())
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
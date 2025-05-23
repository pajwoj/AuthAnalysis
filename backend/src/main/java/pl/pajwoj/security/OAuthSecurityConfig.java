package pl.pajwoj.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import pl.pajwoj.oauth.GoogleOAuthCredentials;
import pl.pajwoj.responses.APIResponse;
import pl.pajwoj.services.OAuthUserServiceImpl;

@ConditionalOnProperty(name = "auth.type", havingValue = "oauth")
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class OAuthSecurityConfig {
    private final GoogleOAuthCredentials googleCredentials;
    private final OAuthUserServiceImpl oauthUserService;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
    }

    private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("google")
                .clientId(googleCredentials.getClient_id())
                .clientSecret(googleCredentials.getClient_secret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(googleCredentials.getRedirect_uris().getFirst())
                .scope("profile", "email")
                .authorizationUri(googleCredentials.getAuth_uri())
                .tokenUri(googleCredentials.getToken_uri())
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .clientName("Google")
                .build();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/user", "/api/config", "/api/logout", "/login/**").permitAll()
                        .requestMatchers("/api/protected").hasAuthority("SECRET")
                        .anyRequest().authenticated())

                .formLogin(AbstractHttpConfigurer::disable)

                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oauthUserService)
                        )
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect("http://localhost:5173/");
                        })
                )

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
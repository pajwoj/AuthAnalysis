package pl.pajwoj.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;

@ConditionalOnProperty(name = "auth.type", havingValue = "oauth")
@RequiredArgsConstructor
@Configuration
public class OAuthClientsConfig {
    private final GoogleOAuthCredentials googleCredentials;
    private final GitHubOAuthCredentials githubCredentials;
    private final GitLabOAuthCredentials gitlabCredentials;
    private final DiscordOAuthCredentials discordCredentials;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(
                googleClientRegistration(),
                githubClientRegistration(),
                gitlabClientRegistration(),
                discordClientRegistration()
        );
    }

    private ClientRegistration githubClientRegistration() {
        return ClientRegistration.withRegistrationId("github")
                .clientId(githubCredentials.getClientId())
                .clientSecret(githubCredentials.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(githubCredentials.getCallback_url())
                .scope("user", "user:email")
                .authorizationUri("https://github.com/login/oauth/authorize")
                .tokenUri("https://github.com/login/oauth/access_token")
                .userInfoUri("https://api.github.com/user")
                .userNameAttributeName("id")
                .clientName("GitHub")
                .build();
    }

    private ClientRegistration discordClientRegistration() {
        return ClientRegistration.withRegistrationId("discord")
                .clientId(discordCredentials.getClientId())
                .clientSecret(discordCredentials.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(discordCredentials.getCallback_url())
                .scope("identify", "email")
                .authorizationUri("https://discord.com/oauth2/authorize")
                .tokenUri("https://discord.com/api/oauth2/token")
                .userInfoUri("https://discord.com/api/users/@me")
                .userNameAttributeName("id")
                .clientName("Discord")
                .build();
    }

    private ClientRegistration gitlabClientRegistration() {
        return ClientRegistration.withRegistrationId("gitlab")
                .clientId(gitlabCredentials.getClientId())
                .clientSecret(gitlabCredentials.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(gitlabCredentials.getCallback_url())
                .scope("profile", "email", "openid")
                .authorizationUri("https://gitlab.com/oauth/authorize")
                .tokenUri("https://gitlab.com/oauth/token")
                .userInfoUri("https://gitlab.com/oauth/userinfo")
                .jwkSetUri("https://gitlab.com/oauth/discovery/keys")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .clientName("GitLab")
                .build();
    }

    private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("google")
                .clientId(googleCredentials.getClient_id())
                .clientSecret(googleCredentials.getClient_secret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(googleCredentials.getRedirect_uris().getFirst())
                .scope("profile", "email", "openid")
                .authorizationUri(googleCredentials.getAuth_uri())
                .tokenUri(googleCredentials.getToken_uri())
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .clientName("Google")
                .build();
    }
}

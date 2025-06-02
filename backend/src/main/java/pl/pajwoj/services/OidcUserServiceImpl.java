package pl.pajwoj.services;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import pl.pajwoj.models.User;
import pl.pajwoj.models.UserAuthority;
import pl.pajwoj.repositories.UserRepository;

import java.util.UUID;

@ConditionalOnProperty(name = "auth.type", havingValue = "oauth")
@RequiredArgsConstructor
@Service
public class OidcUserServiceImpl extends OidcUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        if (oidcUser.getAttribute("email") == null)
            throw new OAuth2AuthenticationException("The email address could not be verified, check your profile settings at the provider you used.");


        return userRepository.findByEmail(oidcUser.getEmail())
                .orElseGet(() -> {
                    User u = new User(
                            oidcUser.getEmail(),
                            passwordEncoder.encode(UUID.randomUUID().toString()),
                            UserAuthority.USER
                    );

                    u.setIdToken(oidcUser.getIdToken());
                    u.setUserInfo(oidcUser.getUserInfo());

                    return userRepository.save(u);
                });
    }
}

package pl.pajwoj.services;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import pl.pajwoj.models.User;
import pl.pajwoj.models.UserAuthority;
import pl.pajwoj.repositories.UserRepository;

import java.util.UUID;

@ConditionalOnProperty(name = "auth.type", havingValue = "oauth")
@RequiredArgsConstructor
@Service
public class OAuthUserServiceImpl extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    
    @Lazy
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        return userRepository.findByEmail(oAuth2User.getAttribute("email"))
                .orElseGet(() -> userRepository.save(new User(
                        oAuth2User.getAttribute("email"),
                        passwordEncoder.encode(UUID.randomUUID().toString()),
                        UserAuthority.USER
                )));
    }
}

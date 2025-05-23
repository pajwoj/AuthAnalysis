package pl.pajwoj.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pajwoj.dtos.UserDTO;
import pl.pajwoj.jwt.JWTTokenProvider;
import pl.pajwoj.models.User;
import pl.pajwoj.models.UserAuthority;
import pl.pajwoj.repositories.UserRepository;
import pl.pajwoj.responses.APIResponse;
import pl.pajwoj.responses.UserResponse;

import java.time.Duration;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, Optional<AuthenticationManager> authenticationManager, Optional<JWTTokenProvider> jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager.orElse(null);
        this.jwtTokenProvider = jwtTokenProvider.orElse(null);
    }

    @ConditionalOnProperty(name = "auth.type", havingValue = "session")
    public ResponseEntity<?> sessionLogin(UserDTO userDTO, HttpServletRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(userDTO.getEmail());

        if (optionalUser.isEmpty()) {
            return APIResponse.userNotFoundResponse(userDTO.getEmail());
        }

        try {
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword());

            Authentication authentication = authenticationManager.authenticate(authRequest);

            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authentication);

            request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", sc);

            return UserResponse.auth(authentication);
        } catch (BadCredentialsException e) {
            return APIResponse.unauthorizedResponse("INVALID_CREDENTIALS", "Invalid email or password");
        } catch (Exception e) {
            return APIResponse.unauthorizedResponse("AUTHENTICATION_FAILED", "Authentication failed: " + e.getMessage());
        }
    }

    @ConditionalOnProperty(name = "auth.type", havingValue = "jwt")
    public ResponseEntity<?> jwt(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && !jwtTokenProvider.isTokenExpired(token)) {
            return ResponseEntity.ok("Token is valid");
        }

        return APIResponse.invalidToken();
    }

    @ConditionalOnProperty(name = "auth.type", havingValue = "jwt")
    public ResponseEntity<?> JWTLogin(UserDTO userDTO, HttpServletResponse response) {
        Optional<User> optionalUser = userRepository.findByEmail(userDTO.getEmail());

        if (optionalUser.isEmpty()) {
            return APIResponse.userNotFoundResponse(userDTO.getEmail());
        }

        try {
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword());

            Authentication authentication = authenticationManager.authenticate(authRequest);

            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authentication);

            String jwt = jwtTokenProvider.generateToken(authentication);

            ResponseCookie cookie = ResponseCookie.from("JWT", jwt)
                    .path("/")
                    .maxAge(Duration.ofDays(7).toSeconds())
                    .secure(true)
                    .httpOnly(true)
                    .sameSite("Strict")
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                    .body(jwt);
        } catch (BadCredentialsException e) {
            return APIResponse.unauthorizedResponse("INVALID_CREDENTIALS", "Invalid email or password");
        } catch (Exception e) {
            return APIResponse.unauthorizedResponse("AUTHENTICATION_FAILED", "Authentication failed: " + e.getMessage());
        }
    }

    @ConditionalOnProperty(name = "auth.type", havingValue = "jwt")
    public ResponseEntity<?> JWTLogout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("JWT", "")
                .path("/")
                .maxAge(0)
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.setHeader("Clear-Site-Data", "\"cookies\"");

        return ResponseEntity.ok("Logout successful");
    }

    public ResponseEntity<?> getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context == null || context.getAuthentication() == null || !context.getAuthentication().isAuthenticated() || "anonymousUser".equals(context.getAuthentication().getPrincipal()))
            return APIResponse.unauthorizedResponse("NOT_LOGGED_IN", "You are not logged in");

        Authentication auth = context.getAuthentication();
        return UserResponse.auth(auth);
    }

    public ResponseEntity<?> register(UserDTO userDTO) {
        if (userDTO.getEmail() == null || userDTO.getPassword() == null)
            return APIResponse.emptyFields();

        if (userRepository.existsByEmail(userDTO.getEmail()))
            return APIResponse.userAlreadyExists(userDTO.getEmail());

        User u = userRepository.save(new User(
                userDTO.getEmail(),
                passwordEncoder.encode(userDTO.getPassword()),
                UserAuthority.USER
        ));

        return UserResponse.register(u);
    }

    public ResponseEntity<?> registerAdmin(UserDTO userDTO) {
        if (userDTO.getEmail() == null || userDTO.getPassword() == null)
            return APIResponse.emptyFields();

        if (userRepository.existsByEmail(userDTO.getEmail()))
            return APIResponse.userAlreadyExists(userDTO.getEmail());

        User u = userRepository.save(new User(
                userDTO.getEmail(),
                passwordEncoder.encode(userDTO.getPassword()),
                UserAuthority.SECRET
        ));

        return UserResponse.register(u);
    }

    public ResponseEntity<?> secret() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("SECRET")))
            return UserResponse.auth(auth);

        return APIResponse.forbidden();
    }
}

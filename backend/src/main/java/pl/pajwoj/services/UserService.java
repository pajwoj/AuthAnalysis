package pl.pajwoj.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import pl.pajwoj.dtos.UserDTO;
import pl.pajwoj.jwt.JWTTokenProvider;
import pl.pajwoj.models.User;
import pl.pajwoj.models.UserAuthority;
import pl.pajwoj.repositories.UserRepository;
import pl.pajwoj.responses.APIResponse;

import java.time.Duration;
import java.util.Map;
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
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(APIResponse.of("User with email: " + userDTO.getEmail() + " not found."));
        }

        try {
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword());

            Authentication authentication = authenticationManager.authenticate(authRequest);

            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authentication);

            request.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(APIResponse.of("Login successful", Map.of(
                            "email", authentication.getName(),
                            "roles", authentication.getAuthorities()
                                    .stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .toList()
                    )));
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(APIResponse.of("Invalid email or password"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(APIResponse.of("Authentication failed"));
        }
    }

    @ConditionalOnProperty(name = "auth.type", havingValue = "jwt")
    public ResponseEntity<?> JWTLogin(UserDTO userDTO, HttpServletResponse response) {
        Optional<User> optionalUser = userRepository.findByEmail(userDTO.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(APIResponse.of("User with email: " + userDTO.getEmail() + " not found."));
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

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                    .body(APIResponse.of("Login successful", Map.of(
                            "email", authentication.getName(),
                            "roles", authentication.getAuthorities()
                                    .stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .toList()
                    )));
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(APIResponse.of("Invalid email or password"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(APIResponse.of("Authentication failed"));
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

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(APIResponse.of("Logout successful"));
    }

    public ResponseEntity<?> getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context == null || context.getAuthentication() == null || !context.getAuthentication().isAuthenticated() || "anonymousUser".equals(context.getAuthentication().getPrincipal()))
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(APIResponse.of("You are not logged in"));

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(APIResponse.of("Login successful", Map.of(
                        "email", context.getAuthentication().getName(),
                        "roles", context.getAuthentication().getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList()
                )));
    }

    public ResponseEntity<?> register(UserDTO userDTO, UserAuthority authority) {
        if (userDTO.getEmail() == null || userDTO.getPassword() == null)
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(APIResponse.of("Fill all required fields"));

        if (userRepository.existsByEmail(userDTO.getEmail()))
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(APIResponse.of("User with email: " + userDTO.getEmail() + " already exists."));

        User u = userRepository.save(new User(
                userDTO.getEmail(),
                passwordEncoder.encode(userDTO.getPassword()),
                authority
        ));

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(APIResponse.of("User registered successfully", Map.of(
                        "email", u.getEmail(),
                        "roles", u.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList()
                )));
    }

    public ResponseEntity<?> secret() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("SECRET")))
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(APIResponse.of("Permitted", Map.of(
                            "email", auth.getName(),
                            "roles", auth.getAuthorities()
                                    .stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .toList()
                    )));

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(APIResponse.of("Only admins can access this page"));
    }
}

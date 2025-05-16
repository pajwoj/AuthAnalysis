package pl.pajwoj.services;

import jakarta.servlet.http.HttpServletRequest;
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
import pl.pajwoj.models.User;
import pl.pajwoj.models.UserAuthority;
import pl.pajwoj.repositories.UserRepository;
import pl.pajwoj.responses.ErrorResponse;
import pl.pajwoj.responses.UserResponse;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<?> login(UserDTO userDTO, HttpServletRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(userDTO.getEmail());

        if (optionalUser.isEmpty()) {
            return ErrorResponse.userNotFoundResponse(userDTO.getEmail());
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
            return ErrorResponse.unauthorizedResponse("INVALID_CREDENTIALS", "Invalid email or password");
        } catch (Exception e) {
            return ErrorResponse.unauthorizedResponse("AUTHENTICATION_FAILED", "Authentication failed: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context == null || context.getAuthentication() == null || !context.getAuthentication().isAuthenticated() || "anonymousUser".equals(context.getAuthentication().getPrincipal()))
            return ErrorResponse.unauthorizedResponse("MISSING_SESSION", "You are not logged in");

        Authentication auth = context.getAuthentication();
        return UserResponse.auth(auth);
    }

    public ResponseEntity<?> register(UserDTO userDTO) {
        if (userDTO.getEmail() == null || userDTO.getPassword() == null)
            return ErrorResponse.emptyFields();

        if (userRepository.existsByEmail(userDTO.getEmail()))
            return ErrorResponse.userAlreadyExists(userDTO.getEmail());

        User u = userRepository.save(new User(
                userDTO.getEmail(),
                passwordEncoder.encode(userDTO.getPassword()),
                UserAuthority.USER
        ));

        return UserResponse.register(u);
    }

    public ResponseEntity<?> registerAdmin(UserDTO userDTO) {
        if (userDTO.getEmail() == null || userDTO.getPassword() == null)
            return ErrorResponse.emptyFields();

        if (userRepository.existsByEmail(userDTO.getEmail()))
            return ErrorResponse.userAlreadyExists(userDTO.getEmail());

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

        return ErrorResponse.forbidden();
    }
}

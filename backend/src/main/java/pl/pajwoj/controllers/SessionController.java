package pl.pajwoj.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pajwoj.models.User;
import pl.pajwoj.models.UserRole;
import pl.pajwoj.repositories.UserRepository;
import pl.pajwoj.responses.ErrorResponse;
import pl.pajwoj.responses.UserResponse;

@RestController
@RequestMapping("/api")
public class SessionController {
    private final UserRepository userRepository;

    public SessionController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated())
            return ErrorResponse.unauthorizedResponse("NO_SESSION", "No active session found!");

        return UserResponse.ok(auth);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(HttpServletRequest request) {
        User user = userRepository.findByEmail("test")
                .orElseGet(() -> userRepository.save(
                        new User("test",
                                "test",
                                UserRole.USER)
                ));

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        request.getSession().setAttribute(
                "SPRING_SECURITY_CONTEXT",
                SecurityContextHolder.getContext()
        );

        System.out.println("Initialized test user for this JVM instance");

        return ResponseEntity.ok(user.toString());
    }
}

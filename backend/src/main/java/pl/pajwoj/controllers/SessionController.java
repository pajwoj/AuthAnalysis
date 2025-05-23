package pl.pajwoj.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import pl.pajwoj.dtos.UserDTO;
import pl.pajwoj.services.UserService;

@ConditionalOnProperty(name = "auth.type", havingValue = "session")
@RestController
@RequestMapping("/api")
public class SessionController {
    private final UserService userService;

    public SessionController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        return userService.sessionLogin(userDTO, request);
    }

    @GetMapping("/protected")
    public ResponseEntity<?> secret() {
        return userService.secret();
    }

    @GetMapping("/csrf")
    public ResponseEntity<?> csrf(HttpServletRequest request) {
        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        return ResponseEntity.ok(csrf.getToken());
    }
}

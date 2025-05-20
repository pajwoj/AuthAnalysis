package pl.pajwoj.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pajwoj.dtos.UserDTO;
import pl.pajwoj.services.UserService;

@ConditionalOnProperty(name = "auth.type", havingValue = "jwt")
@RestController
@RequestMapping("/api")
public class JWTController {
    private final UserService userService;

    public JWTController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/protected")
    public ResponseEntity<?> secret() {
        return userService.secret();
    }

    @PostMapping("/jwt")
    public ResponseEntity<?> jwt(HttpServletRequest request) {
        return userService.jwt(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        return userService.JWTLogin(userDTO, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        return userService.JWTLogout(response);
    }
}

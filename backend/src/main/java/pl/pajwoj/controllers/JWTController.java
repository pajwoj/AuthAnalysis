package pl.pajwoj.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.pajwoj.dtos.UserDTO;
import pl.pajwoj.services.UserService;

@ConditionalOnProperty(name = "auth.type", havingValue = "jwt")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Validated
public class JWTController {
    private final UserService userService;

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping(value = "/protected", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> secret() {
        return userService.secret();
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO, HttpServletResponse response, HttpServletRequest request) {
        return userService.JWTLogin(userDTO, response, request);
    }

    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> logout() {
        return userService.JWTLogout();
    }
}

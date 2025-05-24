package pl.pajwoj.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.pajwoj.dtos.UserDTO;
import pl.pajwoj.responses.APIResponse;
import pl.pajwoj.services.UserService;

@ConditionalOnProperty(name = "auth.type", havingValue = "session")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Validated
public class SessionController {
    private final UserService userService;

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody @Valid UserDTO userDTO, HttpServletRequest request) {
        return userService.sessionLogin(userDTO, request);
    }

    @GetMapping(value = "/protected", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> secret() {
        return userService.secret();
    }

    @GetMapping(value = "/csrf", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> csrf(HttpServletRequest request) {
        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        return ResponseEntity.ok(APIResponse.of(csrf.getToken()));
    }
}

package pl.pajwoj.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pajwoj.services.UserService;

@ConditionalOnProperty(name = "auth.type", havingValue = "oauth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OAuthController {
    private final UserService userService;

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping(value = "/protected", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> secret() {
        return userService.secret();
    }
}

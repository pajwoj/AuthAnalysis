package pl.pajwoj.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pajwoj.dtos.UserDTO;
import pl.pajwoj.services.UserService;

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
        return userService.login(userDTO, request);
    }

    @GetMapping("/protected")
    public ResponseEntity<?> secret() {
        return userService.secret();
    }

    @PostMapping("/csrf")
    public void csrf() {
        //only to generate a csrf token
    }
}

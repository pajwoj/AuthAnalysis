package pl.pajwoj.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PropertyController {
    @Value("${auth.type}")
    private String authType;

    @GetMapping("/config")
    public ResponseEntity<?> config(HttpServletResponse response) {
//        ResponseCookie cookie = ResponseCookie.from("AuthType", authType)
//                .path("/")
//                .maxAge(86400)
//                .secure(true)
//                .httpOnly(true)
//                .sameSite("Strict")
//                .build();
//
//        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(authType);
    }
}

package pl.pajwoj.responses;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import pl.pajwoj.models.User;

import java.time.LocalDateTime;
import java.util.Map;

public class UserResponse {
    public static ResponseEntity<?> auth(Authentication auth) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "email", auth.getName(),
                        "roles", auth.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList()
                ));
    }

    public static ResponseEntity<?> register(User user) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "email", user.getEmail(),
                        "roles", user.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList()
                ));
    }
}

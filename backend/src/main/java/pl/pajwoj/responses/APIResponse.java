package pl.pajwoj.responses;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class APIResponse {
    public static String jsonString(String errorMessage, String message) {
        Map<String, String> json = new HashMap<>();

        json.put("timestamp", LocalDateTime.now().toString());
        json.put("error", errorMessage);
        json.put("message", message);

        return new Gson().toJson(json);
    }

    public static ResponseEntity<?> unauthorizedResponse(String statusMessage, String message) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "error", statusMessage,
                        "message", message
                ));
    }

    public static ResponseEntity<?> userNotFoundResponse(String email) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "error", "NOT_FOUND",
                        "message", "User with email: " + email + " not found"
                ));
    }

    public static ResponseEntity<?> userAlreadyExists(String email) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "error", "ALREADY_EXISTS",
                        "message", "User with email: " + email + " already exists"
                ));
    }

    public static ResponseEntity<?> emptyFields() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "error", "EMPTY_FIELDS",
                        "message", "Fill all required fields"
                ));
    }

    public static ResponseEntity<?> invalidToken() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "error", "INVALID_TOKEN",
                        "message", "Token is invalid or expired"
                ));
    }

    public static ResponseEntity<?> forbidden() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "error", "FORBIDDEN",
                        "message", "You don't have access to this resource"
                ));
    }
}

package pl.pajwoj.responses;

import com.google.gson.Gson;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ErrorResponse {
    public static ResponseEntity<?> unauthorizedResponse(String statusmessage, String message) {
        return ResponseEntity
                .status(401)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "error", statusmessage,
                        "message", message
                ));
    }

    public static String unauthorizedJson(String statusMessage, String message) {
        Map<String, String> json = new HashMap<>();

        json.put("timestamp", LocalDateTime.now().toString());
        json.put("error", statusMessage);
        json.put("message", message);

        return new Gson().toJson(json);
    }
}

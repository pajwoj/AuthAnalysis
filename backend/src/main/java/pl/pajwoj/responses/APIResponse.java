package pl.pajwoj.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
public class APIResponse<T> {
    String timestamp = OffsetDateTime.now().toString();
    String message;
    T data;

    public static APIResponse<Void> of(String message) {
        return new APIResponse<>(message, null);
    }

    public static <T> APIResponse<T> of(String message, T data) {
        return new APIResponse<>(message, data);
    }

    public static String json(String message) {
        return new Gson().toJson(of(message));
    }

    public static <T> String json(String message, T data) {
        return new Gson().toJson(of(message, data));
    }
}
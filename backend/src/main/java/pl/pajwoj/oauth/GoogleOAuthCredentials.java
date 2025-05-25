package pl.pajwoj.oauth;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@ConditionalOnProperty(name = "auth.type", havingValue = "oauth")
@Component
@Getter
public class GoogleOAuthCredentials {
    private String client_id;
    private String project_id;
    private String auth_uri;
    private String token_uri;
    private String auth_provider_x509_cert_url;
    private String client_secret;
    private List<String> redirect_uris = new ArrayList<>();
    private List<String> javascript_origins = new ArrayList<>();

    @PostConstruct
    private void init() {
        try {
            JsonObject json = JsonParser.parseReader(new InputStreamReader(new ClassPathResource("google.json").getInputStream())).getAsJsonObject().get("web").getAsJsonObject();

            this.client_id = json.get("client_id").getAsString();
            this.project_id = json.get("project_id").getAsString();
            this.auth_uri = json.get("auth_uri").getAsString();
            this.token_uri = json.get("token_uri").getAsString();
            this.auth_provider_x509_cert_url = json.get("auth_provider_x509_cert_url").getAsString();
            this.client_secret = json.get("client_secret").getAsString();
            this.redirect_uris = json.getAsJsonArray("redirect_uris").asList().stream()
                    .map(JsonElement::getAsString)
                    .toList();

            this.javascript_origins = json.getAsJsonArray("javascript_origins").asList().stream()
                    .map(JsonElement::getAsString)
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load Google OAuth credentials", e);
        }
    }
}
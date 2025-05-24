package pl.pajwoj.oauth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;

@ConditionalOnProperty(name = "auth.type", havingValue = "oauth")
@Component
@Getter
public class GitHubOAuthCredentials {
    private final String callback_url = "http://localhost:8080/login/oauth2/code/github";
    private String clientSecret;
    private String clientId;

    @PostConstruct
    private void init() {
        try {
            JsonObject webObject = JsonParser.parseReader(new InputStreamReader(new ClassPathResource("github.json").getInputStream())).getAsJsonObject();

            this.clientId = webObject.get("client").getAsString();
            this.clientSecret = webObject.get("secret").getAsString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load GitHub OAuth credentials", e);
        }
    }
}
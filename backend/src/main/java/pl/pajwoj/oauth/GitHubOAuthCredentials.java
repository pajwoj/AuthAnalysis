package pl.pajwoj.oauth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;

@ConditionalOnProperty(name = "auth.type", havingValue = "oauth")
@Component
@Getter
@ToString
public class GitHubOAuthCredentials {
    private final String callback_url = "http://localhost:8080/login/oauth2/code/github";
    private String secret;
    private String client;

    @PostConstruct
    private void init() {
        try {
            JsonObject webObject = JsonParser.parseReader(new InputStreamReader(new ClassPathResource("github.json").getInputStream())).getAsJsonObject();

            this.client = webObject.get("client").getAsString();
            this.secret = webObject.get("secret").getAsString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load GitHub OAuth credentials", e);
        }
    }
}
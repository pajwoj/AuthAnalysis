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
public class DiscordOAuthCredentials {
    private final String callback_url = "http://localhost:8080/login/oauth2/code/discord";
    private String clientSecret;
    private String clientId;

    @PostConstruct
    private void init() {
        try {
            JsonObject json = JsonParser.parseReader(new InputStreamReader(new ClassPathResource("discord.json").getInputStream())).getAsJsonObject();

            this.clientId = json.get("client").getAsString();
            this.clientSecret = json.get("secret").getAsString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load Discord OAuth credentials", e);
        }
    }
}
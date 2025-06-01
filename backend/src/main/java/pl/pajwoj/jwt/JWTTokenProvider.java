package pl.pajwoj.jwt;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

@ConditionalOnProperty(name = "auth.type", havingValue = "jwt")
@Component
public class JWTTokenProvider {

    private SecretKey secretKey;

    @PostConstruct
    public void init() throws IOException {
        JsonObject json = JsonParser.parseReader(new InputStreamReader(new ClassPathResource("jwtSecret.json").getInputStream())).getAsJsonObject();
        String secret = json.get("secret").getAsString();
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(Authentication authentication, HttpServletRequest request) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String userAgent = request.getHeader("User-Agent");
        String userAgentHash = DigestUtils.md5DigestAsHex(userAgent.getBytes(StandardCharsets.UTF_8));

        Instant now = Instant.now();
        Instant expiration = now.plus(15, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(authentication.getName())
                .claim("authorities", authorities)
                .claim("userAgent", userAgentHash)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean validateToken(String token, HttpServletRequest request) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            Claims claims = extractClaims(token);
            String userAgentClaim = claims.get("userAgent", String.class);
            String userAgentRequest = DigestUtils.md5DigestAsHex(request.getHeader("User-Agent").getBytes(StandardCharsets.UTF_8));

            if (!userAgentClaim.equals(userAgentRequest))
                return false;

            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
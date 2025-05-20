package pl.pajwoj.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pl.pajwoj.responses.APIResponse;

import java.io.IOException;

@ConditionalOnProperty(name = "auth.type", havingValue = "jwt")
@Component
@RequiredArgsConstructor
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(APIResponse.jsonString("AUTHENTICATION_FAILED", "Authentication failed: " + authException.getMessage()));
    }
}
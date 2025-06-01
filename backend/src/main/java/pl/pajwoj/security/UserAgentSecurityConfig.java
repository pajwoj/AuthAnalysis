package pl.pajwoj.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.pajwoj.responses.APIResponse;

import java.io.IOException;
import java.util.Objects;

@Component
public class UserAgentSecurityConfig extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/login") || requestURI.equals("/api/csrf") || requestURI.equals("/api/config") || requestURI.equals("/api/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("USER_AGENT") != null) {
            String storedUserAgent = (String) session.getAttribute("USER_AGENT");
            String currentUserAgent = request.getHeader("User-Agent");

            if (!Objects.equals(storedUserAgent, currentUserAgent)) {
                session.invalidate();

                response.setHeader("Clear-Site-Data", "\"cookies\"");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(APIResponse.json("Security check failed. You have been logged out."));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

package pl.pajwoj.security;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.COOKIES;

@Configuration
@EnableWebSecurity
public class SessionSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login", "/login", "/", "/api/logout").permitAll()
                        .anyRequest().authenticated())

                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .expiredSessionStrategy((event) -> {
                            val response = event.getResponse();
                            Map<String, String> jsonResponse = new HashMap<>();
                            jsonResponse.put("error", "SESSION_EXPIRED");
                            jsonResponse.put("message", "Session expired! Log in again. Redirecting to homepage...");

                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(new Gson().toJson(jsonResponse));
                        })
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/api/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/?error=true")
                        .permitAll())

                .logout(logout -> logout.
                        logoutUrl("/api/logout")
                        .addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(COOKIES)))
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("Logout successful! Redirecting to homepage...");
                        })
                        .permitAll()
                )

        ;

        return http.build();
    }
}

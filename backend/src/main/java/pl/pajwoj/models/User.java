package pl.pajwoj.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Setter
@Getter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails, Serializable, OAuth2User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserAuthority authority;

    public User(String email, String password, UserAuthority authority) {
        this.email = email;
        this.password = password;
        this.authority = authority;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of("email", email, "authorities", authority.name());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(authority.toString()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return email;
    }
}

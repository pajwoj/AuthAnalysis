package pl.pajwoj.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pajwoj.models.User;
import pl.pajwoj.models.UserRole;
import pl.pajwoj.repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String password) {
        User user = new User(email, passwordEncoder.encode(password), UserRole.USER);
        return userRepository.save(user);
    }

    public User registerAdmin(String email, String password) {
        User user = new User(email, passwordEncoder.encode(password), UserRole.ADMIN);
        return userRepository.save(user);
    }
}

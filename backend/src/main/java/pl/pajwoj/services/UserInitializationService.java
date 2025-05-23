package pl.pajwoj.services;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import pl.pajwoj.dtos.UserDTO;
import pl.pajwoj.repositories.UserRepository;

@Service
public class UserInitializationService {
    private final UserRepository userRepository;
    private final UserService userService;

    public UserInitializationService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        if (!userRepository.existsByEmail("a"))
            userService.register(new UserDTO("a", "a"));

        if (!userRepository.existsByEmail("admin"))
            userService.registerAdmin(new UserDTO("admin", "admin"));

        if (!userRepository.existsByEmail("ninja2115polska@gmail.com"))
            userService.registerAdmin(new UserDTO("ninja2115polska@gmail.com", "a"));
    }
}

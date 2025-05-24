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
            userService.register(UserDTO.builder()
                    .email("a")
                    .password("a")
                    .build()
            );

        if (!userRepository.existsByEmail("admin"))
            userService.registerAdmin(UserDTO.builder()
                    .email("admin")
                    .password("admin")
                    .build()
            );

        if (!userRepository.existsByEmail("pajwoj@gmail.com"))
            userService.registerAdmin(UserDTO.builder()
                    .email("pajwoj@gmail.com")
                    .password("admin")
                    .build()
            );
    }
}

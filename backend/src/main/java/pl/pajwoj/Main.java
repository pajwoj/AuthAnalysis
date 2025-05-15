package pl.pajwoj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import pl.pajwoj.dtos.UserDTO;
import pl.pajwoj.repositories.UserRepository;
import pl.pajwoj.services.UserService;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Main.class, args);

        UserService userService = context.getBean(UserService.class);

        if (!context.getBean(UserRepository.class).existsByEmail("admin")) {
            userService.registerAdmin(new UserDTO("admin", "admin"));
        }
        
        if (!context.getBean(UserRepository.class).existsByEmail("a")) {
            userService.register(new UserDTO("a", "a"));
        }
    }
}
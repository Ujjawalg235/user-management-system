package com.example.UserManagementSystem.config;

import com.example.UserManagementSystem.entity.User;
import com.example.UserManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("No users found in database. Initializing default admin user...");
            User admin = User.builder()
                    .username("admin")
                    .firstName("Admin")
                    .lastName("System")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("adminpassword"))
                    .role("ROLE_ADMIN")
                    .active(true)
                    .build();
            userRepository.save(admin);
            log.info("Default admin user initialized successfully.");
        }
    }
}

package com.mutualfund.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mutualfund.model.entity.User;
import com.mutualfund.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Initializes the database with default data at application startup. Creates an admin user if it
 * doesn't already exist.
 *
 * @author Mutual Fund Management System
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Executes database initialization logic at application startup. Creates the default admin user
     * if not already present.
     *
     * @param args command line arguments passed to the application
     * @throws Exception if initialization fails
     */
    @Override
    public void run(String... args) throws Exception {
        initializeAdminUser();
    }

    /**
     * Creates the default admin user if it doesn't exist in the database. Uses credentials:
     * username=admin, password=admin123, role=ADMIN
     */
    private void initializeAdminUser() {
        String adminUsername = "admin";

        if (!userRepository.existsByUsername(adminUsername)) {
            User admin =
                    User.builder()
                            .username(adminUsername)
                            .password(passwordEncoder.encode("admin123"))
                            .role(User.Role.ADMIN)
                            .build();

            userRepository.save(admin);
            log.info("Admin user created successfully with username: {}", adminUsername);
        } else {
            log.info("Admin user already exists, skipping initialization");
        }
    }
}

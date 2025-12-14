package com.drivesmart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.drivesmart.entity.User;
import com.drivesmart.entity.UserRole;
import com.drivesmart.service.UserService;

@Component
@Profile("dev")
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserService userService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Load demo users
        loadDemoUsers();
    }

    private void loadDemoUsers() {
        // Admin user
        if (!userService.existsByEmail("admin@drivesmart.com")) {
            User admin = new User();
            admin.setEmail("admin@drivesmart.com");
            admin.setFullName("Admin User");
            admin.setPhoneNumber("+1234567890");
            admin.setRole(UserRole.ADMIN);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setIsActive(true);
            userService.saveUser(admin);
        }

        if (!userService.existsByEmail("worker@drivesmart.com")) {
            User worker = new User();
            worker.setEmail("worker@drivesmart.com");
            worker.setFullName("Staff Member");
            worker.setPhoneNumber("+1234567891");
            worker.setRole(UserRole.WORKER);
            worker.setPassword(passwordEncoder.encode("worker123"));
            worker.setIsActive(true);
            userService.saveUser(worker);
        }

        if (!userService.existsByEmail("user@drivesmart.com")) {
            User customer = new User();
            customer.setEmail("user@drivesmart.com");
            customer.setFullName("John Customer");
            customer.setPhoneNumber("+1234567892");
            customer.setRole(UserRole.CUSTOMER);
            customer.setPassword(passwordEncoder.encode("user123"));
            customer.setIsActive(true);
            userService.saveUser(customer);
        }
    }

    
}

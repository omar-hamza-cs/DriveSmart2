package com.drivesmart.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.drivesmart.entity.User;
import com.drivesmart.entity.UserRole;
import com.drivesmart.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // REMOVE PasswordEncoder from here

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User saveUser(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        return userRepository.save(user);
    }

    public User registerUser(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        String email = Objects.requireNonNull(user.getEmail(), "Email cannot be null");
        if (existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        user.setIsActive(true);
        return userRepository.save(user);
    }

    public Optional<User> findById(@NonNull Long id) {
        Objects.requireNonNull(id, "User ID cannot be null");
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(@NonNull Long id) {
        userRepository.deleteById(id);
    }

    public long countUsers() {
        return userRepository.count();
    }

    // Admin Methods
    public Optional<User> findByUsername(String username) {
        return findByEmail(username); // For Spring Security compatibility
    }

    public void toggleUserStatus(@NonNull Long id) {
        User user = findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
    }

    public Map<String, Object> generateUserActivityReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();
        List<User> allUsers = getAllUsers();

        // Simple activity report (in a real app, you'd track login times, etc.)
        report.put("totalUsers", allUsers.size());
        report.put("activeUsers", allUsers.stream().filter(User::getIsActive).count());
        report.put("inactiveUsers", allUsers.stream().filter(u -> !u.getIsActive()).count());
        report.put("newUsers", 0); // Would need creation date tracking

        return report;
    }
}

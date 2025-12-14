package com.drivesmart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivesmart.entity.User;
import com.drivesmart.entity.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(UserRole role);
    
    Optional<User> findByEmailAndIsActiveTrue(String email); // ACTIVE USERS ONLY
    
    // Optional: If you want to count by role
    long countByRole(UserRole role);
    
    // Optional: Find all active users
    List<User> findByIsActiveTrue();
}
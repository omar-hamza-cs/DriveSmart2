package com.drivesmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.drivesmart.entity.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByIsAvailableTrue();
    
    boolean existsByLicensePlate(String licensePlate);
    
    // Add this method to fix the error
    boolean existsByLicensePlateIgnoreCase(String licensePlate);
    
    List<Car> findByBrandContainingIgnoreCase(String brand);
    
    // Add this custom query method to fix the error
    @Query("SELECT c FROM Car c WHERE " +
           "LOWER(c.brand) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.model) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.licensePlate) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.color) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Car> searchCars(@Param("query") String query);
}
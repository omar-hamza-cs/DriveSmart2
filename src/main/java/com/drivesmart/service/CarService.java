package com.drivesmart.service;

import java.util.List;
import java.util.Optional;

import com.drivesmart.entity.Car;

public interface CarService {
    List<Car> getAllCars();
    Optional<Car> getCarById(Long id);
    Car saveCar(Car car);
    void deleteCar(Long id);
    boolean licensePlateExists(String licensePlate);
    List<Car> searchCars(String query);
    List<Car> searchCarsByBrand(String brand);
    void updateCarStatus(Long id, boolean available);
    List<Car> getAvailableCars();
}
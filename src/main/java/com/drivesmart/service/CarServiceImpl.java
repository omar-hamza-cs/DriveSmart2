package com.drivesmart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.drivesmart.entity.Car;
import com.drivesmart.repository.CarRepository;

@Service
@Transactional
public class CarServiceImpl implements CarService {
    
    @Autowired
    private CarRepository carRepository;

    @Override
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @Override
    public Optional<Car> getCarById(Long id) {
        // Fixed null safety warning
        if (id == null) {
            return Optional.empty();
        }
        return carRepository.findById(id);
    }

    @Override
    public Car saveCar(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Car cannot be null");
        }
        return carRepository.save(car);
    }

    @Override
    public void deleteCar(Long id) {
        // Fixed null safety warning
        if (id != null) {
            carRepository.deleteById(id);
        }
    }

    @Override
    public boolean licensePlateExists(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            return false;
        }
        return carRepository.existsByLicensePlateIgnoreCase(licensePlate.trim());
    }

    @Override
    public List<Car> searchCars(String query) {
        if (query == null || query.trim().isEmpty()) {
            return carRepository.findAll();
        }
        return carRepository.searchCars(query.trim());
    }

    @Override
    public List<Car> searchCarsByBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            return carRepository.findAll();
        }
        return carRepository.findByBrandContainingIgnoreCase(brand.trim());
    }

    @Override
    @Transactional
    public void updateCarStatus(Long id, boolean available) {
        // Fixed null safety warning
        if (id == null) {
            return;
        }
        
        Optional<Car> carOpt = carRepository.findById(id);
        if (carOpt.isPresent()) {
            Car car = carOpt.get();
            car.setIsAvailable(available);
            carRepository.save(car);
        }
    }

    @Override
    public List<Car> getAvailableCars() {
        return carRepository.findByIsAvailableTrue();
    }
}
package com.drivesmart.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.drivesmart.entity.Car;
import com.drivesmart.service.CarService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @Autowired
    private com.drivesmart.service.BookingService bookingService;

    // List all cars (Mobile View)
    @GetMapping
    public String listCars(Model model) {
        List<Car> cars = carService.getAllCars();
        model.addAttribute("cars", cars);
        model.addAttribute("totalCars", cars.size());
        long availableCars = cars.stream().filter(c -> Boolean.TRUE.equals(c.getIsAvailable())).count();
        model.addAttribute("availableCars", availableCars);
        long maintenanceCars = cars.stream().filter(c -> Boolean.TRUE.equals(c.getInMaintenance())).count();
        model.addAttribute("maintenanceCars", maintenanceCars);
        long bookedToday = bookingService.countActiveBookings(java.time.LocalDate.now());
        model.addAttribute("bookedCars", bookedToday);
        java.util.Set<Long> bookedTodayCarIds = new java.util.HashSet<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        for (Car c : cars) {
            Long carId = c.getId();
            if (carId != null && bookingService.isCarBookedOnDate(carId, java.util.Objects.requireNonNull(today))) {
                bookedTodayCarIds.add(carId);
            }
        }
        model.addAttribute("bookedTodayCarIds", bookedTodayCarIds);
        return "cars/list";
    }

    // Add new car - Form
    @GetMapping("/add")
    public String showAddCarForm(Model model) {
        model.addAttribute("car", new Car());
        return "cars/add";
    }

    // Add new car - Submit
    @PostMapping("/add")
    public String addCar(@Valid @ModelAttribute Car car, 
                        BindingResult result,
                        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                        Model model) throws IOException {
        
        if (result.hasErrors()) {
            return "cars/add";
        }
        
        if (carService.licensePlateExists(car.getLicensePlate().toUpperCase())) {
            model.addAttribute("error", "License plate already exists");
            return "cars/add";
        }
        
        car.setLicensePlate(car.getLicensePlate().toUpperCase());
        
        // Handle image upload (optional for mobile)
        if (imageFile != null && !imageFile.isEmpty()) {
            car.setImageName(imageFile.getOriginalFilename());
            car.setImageType(imageFile.getContentType());
            car.setImageData(imageFile.getBytes());
        }
        
        carService.saveCar(car);
        return "redirect:/cars?success";
    }

    // Edit car - Form
    @GetMapping("/edit")
    public String showEditForm(@RequestParam Long id, Model model) {
        Optional<Car> car = carService.getCarById(id);
        if (car.isPresent()) {
            model.addAttribute("car", car.get());
            return "cars/edit";
        }
        return "redirect:/cars?error=Car not found";
    }

    // Edit car - Submit
    @PostMapping("/edit")
    public String updateCar(@Valid @ModelAttribute Car car, 
                           BindingResult result,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        
        if (result.hasErrors()) {
            return "cars/edit";
        }
        
        // Handle image update (optional)
        if (imageFile != null && !imageFile.isEmpty()) {
            car.setImageName(imageFile.getOriginalFilename());
            car.setImageType(imageFile.getContentType());
            car.setImageData(imageFile.getBytes());
        }
        
        carService.saveCar(car);
        return "redirect:/cars?success=updated";
    }

    // Car details
    @GetMapping("/details")
    public String carDetails(@RequestParam Long id, Model model) {
        Optional<Car> car = carService.getCarById(id);
        if (car.isPresent()) {
            model.addAttribute("car", car.get());
            return "cars/details";
        }
        return "redirect:/cars?error=Car not found";
    }

    // Mobile search
    @GetMapping("/search")
    public String searchCars(@RequestParam(required = false) String query, Model model) {
        if (query != null && !query.trim().isEmpty()) {
            // Search by brand, model, or license plate
            List<Car> cars = carService.searchCars(query);
            model.addAttribute("cars", cars);
            model.addAttribute("searchTerm", query);
        } else {
            model.addAttribute("cars", carService.getAllCars());
        }
        return "cars/search";
    }

    // Mobile API: Toggle car availability
    @PutMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<?> updateCarStatus(@PathVariable Long id, 
                                           @RequestParam boolean available) {
        try {
            Optional<Car> carOptional = carService.getCarById(id);
            if (carOptional.isPresent()) {
                Car car = carOptional.get();
                car.setIsAvailable(available);
                carService.saveCar(car);
                return ResponseEntity.ok().body("{\"status\":\"success\",\"message\":\"Car status updated\"}");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"status\":\"error\",\"message\":\"Error updating car status\"}");
        }
    }

    // Mobile API: Delete car
    @PostMapping("/{id}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteCar(@PathVariable Long id) {
        try {
            carService.deleteCar(id);
            return ResponseEntity.ok().body("{\"status\":\"success\",\"message\":\"Car deleted\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"status\":\"error\",\"message\":\"Error deleting car\"}");
        }
    }

    // Serve car images
    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getCarImage(@PathVariable Long id) {
        Optional<Car> carOptional = carService.getCarById(id);
        
        if (carOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Car car = carOptional.get();
        byte[] imageData = car.getImageData();
        String imageType = car.getImageType();
        
        if (imageData == null || imageType == null) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            MediaType mediaType = MediaType.parseMediaType(imageType);
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(imageData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Mobile API: Quick add (for mobile app)
    @PostMapping("/quick-add")
    @ResponseBody
    public ResponseEntity<?> quickAddCar(@RequestParam String brand,
                                        @RequestParam String model,
                                        @RequestParam String licensePlate,
                                        @RequestParam Double pricePerDay,
                                        @RequestParam(required = false) Integer year,
                                        @RequestParam(required = false) String color) {
        try {
            // Check if license plate exists
            if (carService.licensePlateExists(licensePlate.toUpperCase())) {
                return ResponseEntity.badRequest()
                        .body("{\"status\":\"error\",\"message\":\"License plate already exists\"}");
            }
            
            Car car = new Car();
            car.setBrand(brand);
            car.setModel(model);
            car.setLicensePlate(licensePlate.toUpperCase());
            car.setPricePerDay(pricePerDay);
            car.setYear(year != null ? year : 2023);
            car.setColor(color != null ? color : "Black");
            car.setIsAvailable(true);
            
            carService.saveCar(car);
            
            return ResponseEntity.ok()
                    .body("{\"status\":\"success\",\"message\":\"Car added successfully\",\"id\":\"" + car.getId() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"status\":\"error\",\"message\":\"Error adding car\"}");
        }
    }
}

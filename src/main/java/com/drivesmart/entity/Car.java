package com.drivesmart.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "cars")
public class Car {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Brand is required")
    @Column(nullable = false)
    private String brand;
    
    @NotBlank(message = "Model is required")
    @Column(nullable = false)
    private String model;
    
    @NotBlank(message = "License plate is required")
    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be after 1900")
    @Max(value = 2030, message = "Year cannot be in the far future")
    @Column(name = "`year`", nullable = false)
    private Integer year;
    
    @NotBlank(message = "Color is required")
    @Column(nullable = false)
    private String color;
    
    @NotNull(message = "Price per day is required")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    @Column(name = "price_per_day", nullable = false)
    private Double pricePerDay;
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "in_maintenance", nullable = false)
    private Boolean inMaintenance = false;
    
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "image_data", length = 10485760) // 10MB max
    private byte[] imageData;
    
    @Column(name = "image_name")
    private String imageName;
    
    @Column(name = "image_type")
    private String imageType;
    
    // Constructors
    public Car() {}
    
    public Car(String brand, String model, String licensePlate, Integer year, 
               String color, Double pricePerDay) {
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
        this.year = year;
        this.color = color;
        this.pricePerDay = pricePerDay;
        this.isAvailable = true;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public Double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(Double pricePerDay) { this.pricePerDay = pricePerDay; }
    
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public Boolean getInMaintenance() { return inMaintenance; }
    public void setInMaintenance(Boolean inMaintenance) { this.inMaintenance = inMaintenance; }
    
    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }
    
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    
    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }
    
    // Helper method for backward compatibility
    public String getImageUrl() {
        if (imageData != null && imageData.length > 0) {
            return "/cars/image/" + this.id;
        }
        return "/images/default-car.png";
    }
    
    // Helper method to check if image exists
    public boolean hasImage() {
        return imageData != null && imageData.length > 0;
    }
    
    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", year=" + year +
                ", color='" + color + '\'' +
                ", pricePerDay=" + pricePerDay +
                ", isAvailable=" + isAvailable +
                ", inMaintenance=" + inMaintenance +
                ", imageName='" + imageName + '\'' +
                ", imageType='" + imageType + '\'' +
                ", hasImage=" + hasImage() +
                '}';
    }
}

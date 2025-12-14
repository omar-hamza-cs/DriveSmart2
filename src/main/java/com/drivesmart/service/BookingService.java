package com.drivesmart.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.drivesmart.entity.Booking;
import com.drivesmart.entity.BookingStatus;
import com.drivesmart.entity.Car;
import com.drivesmart.entity.User;
import com.drivesmart.repository.BookingRepository;
import com.drivesmart.repository.CarRepository;
import com.drivesmart.repository.UserRepository;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getUserBookings(@NonNull Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public Optional<Booking> getBookingById(@NonNull Long id) {
        return bookingRepository.findById(id);
    }

    public Booking createBooking(@NonNull Long userId, @NonNull Long carId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        if (!isCarAvailable(carId, startDate, endDate)) {
            throw new RuntimeException("Car is not available for the selected dates");
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double totalPrice = days * car.getPricePerDay();

        Booking booking = new Booking(user, car, startDate, endDate, totalPrice);
        booking.setStatus(BookingStatus.CONFIRMED);
        
        return bookingRepository.save(booking);
    }

    public Booking updateBookingStatus(@NonNull Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    public void cancelBooking(@NonNull Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    public boolean isCarAvailable(@NonNull Long carId, LocalDate startDate, LocalDate endDate) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));
        if (!car.getIsAvailable() || Boolean.TRUE.equals(car.getInMaintenance())) {
            return false;
        }

        List<BookingStatus> activeStatuses = Arrays.asList(
                BookingStatus.CONFIRMED,
                BookingStatus.ACTIVE,
                BookingStatus.PENDING
        );

        return !bookingRepository
                .existsByCarIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        carId,
                        activeStatuses,
                        endDate,
                        startDate
                );
    }

    public boolean isCarBookedOnDate(@NonNull Long carId, @NonNull LocalDate date) {
        List<BookingStatus> activeStatuses = Arrays.asList(
                BookingStatus.CONFIRMED,
                BookingStatus.ACTIVE,
                BookingStatus.PENDING
        );
        return bookingRepository.findAll().stream()
                .filter(b -> b.getCar().getId().equals(carId))
                .filter(b -> activeStatuses.contains(b.getStatus()))
                .anyMatch(b -> !b.getStartDate().isAfter(date) && !b.getEndDate().isBefore(date));
    }

    // Admin/Reporting Methods
    public long countActiveBookings(LocalDate date) {
        List<BookingStatus> activeStatuses = Arrays.asList(
                BookingStatus.CONFIRMED,
                BookingStatus.ACTIVE
        );
        return bookingRepository.findAll().stream()
                .filter(b -> activeStatuses.contains(b.getStatus()))
                .filter(b -> !b.getStartDate().isAfter(date) && !b.getEndDate().isBefore(date))
                .count();
    }

    public double calculateMonthlyRevenue(LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findAll().stream()
                .filter(b -> !b.getStartDate().isAfter(endDate) && !b.getEndDate().isBefore(startDate))
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.COMPLETED)
                .mapToDouble(Booking::getTotalPrice)
                .sum();
    }

    public Map<String, Object> generateMonthlyReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();
        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(b -> !b.getStartDate().isAfter(endDate) && !b.getEndDate().isBefore(startDate))
                .collect(Collectors.toList());

        report.put("totalBookings", bookings.size());
        report.put("confirmedBookings", bookings.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count());
        report.put("cancelledBookings", bookings.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count());
        report.put("totalRevenue", bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.COMPLETED)
                .mapToDouble(Booking::getTotalPrice).sum());

        return report;
    }

    public Map<String, Object> generateRevenueReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();
        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(b -> !b.getStartDate().isAfter(endDate) && !b.getEndDate().isBefore(startDate))
                .collect(Collectors.toList());

        double totalRevenue = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.COMPLETED)
                .mapToDouble(Booking::getTotalPrice).sum();

        report.put("totalRevenue", totalRevenue);
        report.put("averageBookingValue", bookings.isEmpty() ? 0 : totalRevenue / bookings.size());
        report.put("bookingsCount", bookings.size());

        return report;
    }

    public List<Booking> findRecentBookings(int limit) {
        return bookingRepository.findAll().stream()
                .sorted((b1, b2) -> b2.getId().compareTo(b1.getId())) // Sort by ID (newest first)
                .limit(limit)
                .collect(Collectors.toList());
    }
}

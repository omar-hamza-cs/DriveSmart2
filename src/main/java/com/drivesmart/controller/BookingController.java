package com.drivesmart.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.drivesmart.entity.Booking;
import com.drivesmart.entity.BookingStatus;
import com.drivesmart.entity.Car;
import com.drivesmart.entity.User;
import com.drivesmart.entity.UserRole;
import com.drivesmart.service.BookingService;
import com.drivesmart.service.CarService;
import com.drivesmart.service.UserService;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final CarService carService;
    private final UserService userService;

    public BookingController(
        @NonNull BookingService bookingService,
        @NonNull CarService carService,
        @NonNull UserService userService
    ) {
        this.bookingService = bookingService;
        this.carService = carService;
        this.userService = userService;
    }

    
    @GetMapping("/admin-new")
    public String adminNewBooking(@RequestParam(required = false) Long clientId,
                                  @RequestParam(required = false) Long carId,
                                  Model model) {
        try {
            List<Car> cars = carService.getAllCars();
            List<User> clients = userService.findByRole(UserRole.CUSTOMER);
            model.addAttribute("cars", cars);
            model.addAttribute("clients", clients);
            model.addAttribute("minDate", LocalDate.now().toString());
            if (clientId != null) {
                model.addAttribute("selectedClientId", clientId);
            }
            if (carId != null) {
                model.addAttribute("selectedCarId", carId);
            }
            return "bookings/admin-new";
        } catch (Exception e) {
            throw new RuntimeException("Failed to load admin booking form", e);
        }
    }

    @PostMapping("/admin/create")
    public String adminCreateBooking(
            @RequestParam @NonNull Long clientId,
            @RequestParam @NonNull Long carId,
            @RequestParam @NonNull String startDate,
            @RequestParam @NonNull String endDate,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User client = userService.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            if (client.getRole() != UserRole.CUSTOMER) {
                throw new IllegalArgumentException("Selected user is not a client");
            }
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            Booking booking = bookingService.createBooking(
                    Objects.requireNonNull(clientId, "Client ID cannot be null"),
                    Objects.requireNonNull(carId, "Car ID cannot be null"),
                    start,
                    end);
            redirectAttributes.addFlashAttribute("success", "Booking created successfully");
            return "redirect:/bookings/" + booking.getId();
        } catch (java.time.format.DateTimeParseException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/bookings/admin-new";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/bookings/admin-new";
        }
    }

    @GetMapping
    public String listBookings(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Booking> bookings;
            if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.WORKER) {
                bookings = bookingService.getAllBookings();
                model.addAttribute("isStaff", true);
            } else {
                bookings = bookingService.getUserBookings(Objects.requireNonNull(user.getId(), "User ID cannot be null"));
                model.addAttribute("isStaff", false);
            }

            model.addAttribute("bookings", bookings);
            // Stats for bookings/list header
            model.addAttribute("totalBookings", bookings.size());
            LocalDate today = LocalDate.now();
            model.addAttribute("activeBookings", bookingService.countActiveBookings(today));
            double revenueToday = bookingService.calculateMonthlyRevenue(today, today);
            model.addAttribute("revenueToday", String.format("$%.2f", revenueToday));
            return "bookings/list";
        } catch (Exception e) {
            throw new RuntimeException("Failed to list bookings", e);
        }
    }

    @GetMapping("/new")
    public String showBookingForm(
            @RequestParam @NonNull Long carId, 
            Model model) {
        try {
            Car car = carService.getCarById(Objects.requireNonNull(carId, "Car ID cannot be null"))
                    .orElseThrow(() -> new RuntimeException("Car not found"));
            model.addAttribute("car", car);
            model.addAttribute("minDate", LocalDate.now().toString());
            return "bookings/new";
        } catch (Exception e) {
            throw new RuntimeException("Failed to show booking form", e);
        }
    }

    @PostMapping
    public String createBooking(
            @RequestParam @NonNull Long carId,
            @RequestParam @NonNull String startDate,
            @RequestParam @NonNull String endDate,
            RedirectAttributes redirectAttributes) {
        
        try {
            LocalDate start = LocalDate.parse(Objects.requireNonNull(startDate, "Start date cannot be null"));
            LocalDate end = LocalDate.parse(Objects.requireNonNull(endDate, "End date cannot be null"));
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Long userId = Objects.requireNonNull(user.getId(), "User ID cannot be null");
            Booking booking = bookingService.createBooking(
                userId, 
                carId, 
                start, 
                end
            );
            redirectAttributes.addFlashAttribute("success", "Booking created successfully!");
            return "redirect:/bookings/" + booking.getId();
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid date format. Please use YYYY-MM-DD");
            return "redirect:/bookings/new?carId=" + carId;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid input: " + e.getMessage());
            return "redirect:/bookings/new?carId=" + carId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create booking: " + e.getMessage());
            return "redirect:/bookings/new?carId=" + carId;
        }
    }

    @GetMapping("/{id}")
    public String viewBooking(
            @PathVariable @NonNull Long id,
            Model model) {
        try {
            Booking booking = bookingService.getBookingById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
            model.addAttribute("booking", booking);
            long totalDays = java.time.temporal.ChronoUnit.DAYS.between(
                    booking.getStartDate(), booking.getEndDate()) + 1;
            model.addAttribute("totalDays", totalDays);
            return "bookings/confirmation";
        } catch (Exception e) {
            throw new RuntimeException("Failed to load booking details", e);
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(
            @PathVariable @NonNull Long id, 
            RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id);
            redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel booking: " + e.getMessage());
        }
        return "redirect:/bookings";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable @NonNull Long id,
            @RequestParam @NonNull BookingStatus status,
            RedirectAttributes redirectAttributes) {
        try {
            bookingService.updateBookingStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Booking status updated");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update status: " + e.getMessage());
        }
        return "redirect:/bookings";
    }
}

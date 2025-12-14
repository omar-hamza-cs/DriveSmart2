package com.drivesmart.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.drivesmart.entity.User;
import com.drivesmart.entity.UserRole;
import com.drivesmart.service.BookingService;
import com.drivesmart.service.UserService;
import com.drivesmart.entity.AuditLevel;
import com.drivesmart.service.AuditLogService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private BookingService bookingService;

    @Autowired
    private AuditLogService auditLogService;

    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(username).orElse(null);
    }

    @GetMapping
    public String adminDashboard(Model model) {
        // User statistics
        List<User> allUsers = userService.getAllUsers();
        Map<String, Long> userStats = allUsers.stream()
            .collect(Collectors.groupingBy(
                user -> user.getRole().name(),
                Collectors.counting()
            ));
        
        // Booking statistics
        LocalDate today = LocalDate.now();
        long activeBookings = bookingService.countActiveBookings(today);
        double monthlyRevenue = bookingService.calculateMonthlyRevenue(
            YearMonth.now().atDay(1), 
            YearMonth.now().atEndOfMonth()
        );
        
        java.util.Map<String, Object> attrs = new java.util.HashMap<>();
        attrs.put("totalUsers", (long) allUsers.size());
        attrs.put("userStats", userStats);
        attrs.put("activeBookings", activeBookings);
        attrs.put("monthlyRevenue", monthlyRevenue);
        attrs.put("recentUsers", allUsers.stream().limit(5).collect(Collectors.toList()));
        attrs.put("recentBookings", bookingService.findRecentBookings(5));
        model.addAllAttributes(attrs);
        
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            Model model) {
        
        List<User> users;
        if (role != null && !role.isEmpty()) {
            users = userService.findByRole(UserRole.valueOf(role.toUpperCase()));
        } else {
            users = userService.getAllUsers();
        }
        
        if (status != null && !status.isEmpty()) {
            boolean isActive = "active".equalsIgnoreCase(status);
            users = users.stream()
                .filter(user -> user.getIsActive() == isActive)
                .collect(Collectors.toList());
        }
        
        model.addAttribute("users", users);
        model.addAttribute("userRoles", UserRole.values());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable("id") long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("success", "User status updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user status: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/reports")
    public String viewReports(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "monthly") String reportType,
            Model model) {
        
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        Map<String, Object> reportData;

        switch (reportType.toLowerCase()) {
            case "revenue":
                reportData = bookingService.generateRevenueReport(startDate, endDate);
                break;
            case "user-activity":
                reportData = userService.generateUserActivityReport(startDate, endDate);
                break;
            case "monthly":
            default:
                reportData = bookingService.generateMonthlyReport(startDate, endDate);
                break;
        }

        java.util.Map<String, Object> reportAttrs = new java.util.HashMap<>();
        reportAttrs.put("reportData", reportData);
        reportAttrs.put("startDate", startDate);
        reportAttrs.put("endDate", endDate);
        reportAttrs.put("reportType", reportType);
        reportAttrs.put("reportTypes", java.util.List.of("monthly", "revenue", "user-activity"));
        model.addAllAttributes(reportAttrs);
        
        return "admin/reports";
    }

    @GetMapping("/settings")
    public String systemSettings(Model model) {
        // In a real app, these would be loaded from a configuration service/database
        Map<String, Object> settings = Map.of(
            "appName", "DriveSmart",
            "version", "2.0.0",
            "environment", "Production",
            "maintenanceMode", false,
            "maxLoginAttempts", 5,
            "sessionTimeout", 30,
            "emailNotifications", true,
            "smtpConfigured", true
        );
        
        model.addAttribute("settings", settings);
        return "admin/settings";
    }

    @PostMapping("/settings/update")
    public String updateSettings(
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {
        try {
            // In a real app, save these to a database
            // settingsService.updateSettings(allParams);
            redirectAttributes.addFlashAttribute("success", "Settings updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating settings: " + e.getMessage());
        }
        return "redirect:/admin/settings";
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String severity,
            Model model) {
        AuditLevel level = null;
        if (severity != null && !severity.isBlank()) {
            try {
                level = AuditLevel.valueOf(severity.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        var logs = auditLogService.getAuditLogs(search, level, page, size);
        model.addAttribute("logs", logs);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("severity", severity);
        model.addAttribute("search", search);
        return "admin/audit-logs";
    }
}

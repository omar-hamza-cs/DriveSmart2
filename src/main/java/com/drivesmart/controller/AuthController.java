package com.drivesmart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.drivesmart.entity.User;
import com.drivesmart.entity.UserRole;
import com.drivesmart.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping({"/register", "/auth/register"})
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", UserRole.values());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute User user, 
                             BindingResult result, 
                             Model model,
                             RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("allRoles", UserRole.values());
            return "auth/register";
        }

        try {
            if (user.getRole() == null) {
                user.setRole(UserRole.WORKER);
            }
            
            if (userService.existsByEmail(user.getEmail())) {
                model.addAttribute("error", "Email already registered");
                model.addAttribute("allRoles", UserRole.values());
                return "auth/register";
            }
            
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.saveUser(user);
            
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            model.addAttribute("allRoles", UserRole.values());
            return "auth/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "success", required = false) String success,
            Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        
        if (success != null) {
            model.addAttribute("success", success);
        }
        
        return "auth/login";
    }
    
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        User user = userService.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        model.addAttribute("user", user);
        
        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin";
        } else {
            return "redirect:/";
        }
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout";
    }
}

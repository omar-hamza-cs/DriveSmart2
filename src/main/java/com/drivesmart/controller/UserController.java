package com.drivesmart.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.drivesmart.entity.User;
import com.drivesmart.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/profile")
    public String userProfile(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/edit-profile")
    public String editProfile(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElseThrow();
        model.addAttribute("user", user);
        return "user/edit-profile";
    }

    @PostMapping("/edit-profile")
    public String updateProfile(Authentication authentication,
                               @ModelAttribute("user") User form,
                               Model model) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElseThrow();
        user.setFullName(form.getFullName());
        user.setPhoneNumber(form.getPhoneNumber());
        userService.saveUser(user);
        return "redirect:/user/profile";
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return "user/change-password";
    }

    @PostMapping("/change-password")
    public String performChangePassword(Authentication authentication,
                                        @RequestParam String newPassword,
                                        @RequestParam String confirmPassword,
                                        Model model) {
        if (newPassword == null || newPassword.isBlank() || !newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "user/change-password";
        }
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElseThrow();
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.saveUser(user);
        model.addAttribute("success", "Password updated");
        return "user/change-password";
    }
}

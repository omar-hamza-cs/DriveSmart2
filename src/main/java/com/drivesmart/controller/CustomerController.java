package com.drivesmart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.drivesmart.entity.User;
import com.drivesmart.entity.UserRole;
import com.drivesmart.service.UserService;

@Controller
@RequestMapping("/customers")
@org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','WORKER')")
public class CustomerController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String listCustomers(Model model) {
        List<User> customers = userService.findByRole(UserRole.CUSTOMER);
        model.addAttribute("customers", customers);
        model.addAttribute("totalCustomers", customers.size());
        model.addAttribute("activeCustomers", customers.stream().filter(User::getIsActive).count());
        return "customers/list";
    }

    @GetMapping("/add")
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customer", new User());
        return "customers/add";
    }

    @PostMapping("/add")
    public String addCustomer(@ModelAttribute("customer") User customer,
                              Model model) {
        if (customer.getFullName() == null || customer.getFullName().isBlank() ||
            customer.getEmail() == null || customer.getEmail().isBlank() ||
            customer.getPhoneNumber() == null || customer.getPhoneNumber().isBlank()) {
            model.addAttribute("error", "Please fill all required fields");
            return "customers/add";
        }

        if (userService.existsByEmail(customer.getEmail())) {
            model.addAttribute("error", "Email already exists");
            return "customers/add";
        }

        customer.setRole(UserRole.CUSTOMER);
        if (customer.getPassword() != null && !customer.getPassword().isBlank()) {
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        } else {
            customer.setPassword(passwordEncoder.encode("Temp1234"));
        }
        customer.setIsActive(true);
        userService.saveUser(customer);
        return "redirect:/customers?success";
    }
}

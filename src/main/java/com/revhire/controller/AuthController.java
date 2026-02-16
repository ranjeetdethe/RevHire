package com.revhire.controller;

import com.revhire.model.User;
import com.revhire.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.revhire.dto.UserRegistrationDTO;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserRegistrationDTO registrationDTO,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "register";
        }

        try {
            User.UserRole userRole = User.UserRole.valueOf(registrationDTO.getRole());
            User newUser = userService.registerUser(
                    registrationDTO.getFirstName(),
                    registrationDTO.getLastName(),
                    registrationDTO.getEmail(),
                    registrationDTO.getPassword(),
                    registrationDTO.getPhone(),
                    userRole,
                    registrationDTO.getSecurityQuestion(),
                    registrationDTO.getSecurityAnswer());

            if (newUser != null) {
                return "redirect:/login?registered=true";
            } else {
                model.addAttribute("error", "Registration failed. Please try again.");
                return "register";
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "register";
        }
    }
}

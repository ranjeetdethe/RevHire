package com.revhire.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.revhire.security.CustomUserDetails;
import com.revhire.service.UserService;
import com.revhire.model.User;

@Controller
public class SettingsController {

    private final UserService userService;

    public SettingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/settings")
    public String showSettings(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Fetch fresh user data from DB
        User user = userService.getUserById(userDetails.getId())
                .orElse(userDetails.getUser()); // Fallback to session user if DB fails (unlikely)

        model.addAttribute("user", user);
        model.addAttribute("activePage", "settings");
        return "settings";
    }
}

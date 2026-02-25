package com.revhire.controller;

import com.revhire.dto.request.LoginRequest;
import com.revhire.model.User;
import com.revhire.security.CustomUserDetails;
import com.revhire.security.JwtTokenProvider;
import com.revhire.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
// Maps to both paths to ensure compatibility with any frontend setup
@RequestMapping(value = { "/api/v1/auth", "/api/auth" })
public class AuthRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            String roleStr = user.getRole().name();

            // Expected by Auth.service -> data.token and data.user OR just token and user
            // roots
            String token = tokenProvider.generateToken(user.getId(), user.getEmail(), roleStr);

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", user.getEmail());
            userMap.put("role", roleStr);
            userMap.put("firstName", user.getFirstName());
            userMap.put("lastName", user.getLastName());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userMap);
            response.put("role", roleStr);
            response.put("userId", user.getId());
            response.put("name", user.getFirstName() + " " + user.getLastName());
            response.put("email", user.getEmail());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> payload) {
        try {
            // Check if user exists
            if (userService.findByEmail(payload.get("email")).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email is already in use!"));
            }

            User.UserRole userRole = User.UserRole.valueOf(payload.get("role"));

            // userService.registerUser saves to database, we can just use the signature
            // If they have standard registerUser:
            // The existing code used registerUser(firstName, lastName, email, password,
            // phone, userRole, securityQuestion, securityAnswer)
            // If some fields are missing, we default them
            String firstName = payload.get("firstName");
            String lastName = payload.get("lastName");
            String email = payload.get("email");
            String password = payload.get("password");
            String phone = payload.getOrDefault("phone", "");
            String question = payload.getOrDefault("securityQuestion", "What is your pet's name?");
            String answer = payload.getOrDefault("securityAnswer", "fluffy");

            User newUser = userService.registerUser(
                    firstName,
                    lastName,
                    email,
                    password,
                    phone,
                    userRole,
                    question,
                    answer);

            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null && userDetails.getUser() != null) {
            User user = userDetails.getUser();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", user.getEmail());
            userMap.put("role", user.getRole().name());
            userMap.put("firstName", user.getFirstName());
            userMap.put("lastName", user.getLastName());
            return ResponseEntity.ok(userMap);
        }
        return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
    }
}

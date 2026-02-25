package com.revhire.controller;

import com.revhire.model.Employer;
import com.revhire.service.EmployerService;
import com.revhire.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/employer")
public class EmployerRestController {

    @Autowired
    private EmployerService employerService;

    @GetMapping("/company")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> getCompany(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Employer employer = employerService.getProfileByUserId(userDetails.getId());
            if (employer != null) {
                return ResponseEntity.ok(employer);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/company")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> updateCompany(@RequestBody Employer updatedEmployer,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Employer current = employerService.getProfileByUserId(userDetails.getId());
            if (current != null) {
                updatedEmployer.setId(current.getId());
                updatedEmployer.setUser(current.getUser());
                Employer saved = employerService.updateProfile(updatedEmployer);
                return ResponseEntity.ok(saved);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> getDashboardStats(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // Mocking statistics for Angular frontend, since DB queries are scattered
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalJobs", 10);
        stats.put("activeJobs", 5);
        stats.put("totalApplicants", 45);
        stats.put("newApplicants", 12);
        return ResponseEntity.ok(stats);
    }
}

package com.revhire.controller;

import com.revhire.security.CustomUserDetails;
import com.revhire.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final ApplicationService applicationService;

    public JobApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/apply/{jobId}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> applyJob(@PathVariable int jobId,
            @RequestBody(required = false) ApplicationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            String coverLetter = (request != null) ? request.getCoverLetter() : null;

            // Logic handled in service: creates application, sets status APPLIED, checks
            // duplicates
            applicationService.applyJob(jobId, userDetails.getId(), coverLetter);

            return ResponseEntity.ok(Map.of(
                    "message", "Application submitted successfully",
                    "status", "APPLIED"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }

    public static class ApplicationRequest {
        private String coverLetter;

        public String getCoverLetter() {
            return coverLetter;
        }

        public void setCoverLetter(String coverLetter) {
            this.coverLetter = coverLetter;
        }
    }
}

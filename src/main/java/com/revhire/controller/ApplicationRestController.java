package com.revhire.controller;

import com.revhire.model.Application;
import com.revhire.service.ApplicationService;
import com.revhire.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationRestController {

    @Autowired
    private ApplicationService applicationService;

    // Helper map to convert Application to Angular-compatible object if needed
    private Map<String, Object> toAppDTO(Application app) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", app.getId());
        dto.put("status", app.getStatus());
        dto.put("coverLetter", app.getCoverLetter());
        dto.put("appliedAt", app.getAppliedAt());
        // For My Applications, we need the job summary
        if (app.getJob() != null) {
            Map<String, Object> jobDto = new HashMap<>();
            jobDto.put("id", app.getJob().getId());
            jobDto.put("title", app.getJob().getTitle());
            jobDto.put("location", app.getJob().getLocation());
            if (app.getJob().getEmployer() != null) {
                jobDto.put("companyName", app.getJob().getEmployer().getCompanyName());
            }
            dto.put("job", jobDto);
        }
        if (app.getJobSeeker() != null) {
            Map<String, Object> seekerDto = new HashMap<>();
            seekerDto.put("id", app.getJobSeeker().getId());
            if (app.getJobSeeker().getUser() != null) {
                seekerDto.put("firstName", app.getJobSeeker().getUser().getFirstName());
                seekerDto.put("lastName", app.getJobSeeker().getUser().getLastName());
                seekerDto.put("userId", app.getJobSeeker().getUser().getId());
            } else {
                seekerDto.put("firstName", app.getJobSeeker().getFirstName());
                seekerDto.put("lastName", app.getJobSeeker().getLastName());
            }
            dto.put("seeker", seekerDto);
            // Some frontend models might rely on applicant.name mapped here
            dto.put("applicant", seekerDto);
        }
        return dto;
    }

    @PostMapping("/apply/{jobId}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> applyForJob(@PathVariable("jobId") int jobId,
            @RequestBody(required = false) Map<String, String> payload,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            String coverLetter = payload != null ? payload.getOrDefault("coverLetter", "") : "";
            applicationService.applyJob(jobId, userDetails.getId(), coverLetter);
            return ResponseEntity.ok(Map.of("message", "Application submitted successfully", "status", "APPLIED"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "An error occurred while applying"));
        }
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> getMyApplications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<Application> apps = applicationService.getApplicationsByUserId(userDetails.getId());
            List<Map<String, Object>> content = apps.stream().map(this::toAppDTO).collect(Collectors.toList());
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> getJobApplications(@PathVariable("jobId") int jobId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<Application> apps = applicationService.getApplicationsByJob(jobId);

            // Filter to ensure the endpoint isn't abused to look at other's apps if they
            // exist
            // Handled mostly by service logic, but checking just in case:
            List<Map<String, Object>> content = apps.stream()
                    .filter(app -> app.getJob() != null && app.getJob().getEmployer() != null
                            && app.getJob().getEmployer().getUser().getId() == userDetails.getId())
                    .map(this::toAppDTO).collect(Collectors.toList());

            Map<String, Object> paged = new HashMap<>();
            paged.put("content", content);
            paged.put("totalElements", content.size());
            return ResponseEntity.ok(paged);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> updateStatus(@PathVariable("id") int id, @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Application.ApplicationStatus status = Application.ApplicationStatus.valueOf(payload.get("status"));
            String notes = payload.get("notes");
            boolean updated = applicationService.updateApplicationStatus(id, userDetails.getId(), status, notes);
            if (updated) {
                return ResponseEntity.ok(Map.of("message", "Status updated successfully"));
            }
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to update status. Check job ownership"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid status value"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/check/{jobId}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> checkApplicationStatus(@PathVariable("jobId") int jobId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<Application> apps = applicationService.getApplicationsByUserId(userDetails.getId());
            boolean hasApplied = apps.stream().anyMatch(a -> a.getJob() != null && a.getJob().getId() == jobId);
            return ResponseEntity.ok(Map.of("hasApplied", hasApplied));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error checking application status"));
        }
    }
}

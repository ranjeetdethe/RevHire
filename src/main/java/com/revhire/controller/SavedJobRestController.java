package com.revhire.controller;

import com.revhire.model.SavedJob;
import com.revhire.service.SavedJobService;
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
@RequestMapping("/api/v1/saved-jobs")
@PreAuthorize("hasRole('JOB_SEEKER')")
public class SavedJobRestController {

    @Autowired
    private SavedJobService savedJobService;

    // Helper map to convert SavedJob
    private Map<String, Object> toSavedDto(SavedJob sj) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("savedAt", sj.getSavedAt());
        if (sj.getJob() != null) {
            Map<String, Object> jobDto = new HashMap<>();
            jobDto.put("id", sj.getJob().getId());
            jobDto.put("title", sj.getJob().getTitle());
            jobDto.put("location", "Hybrid");
            if (sj.getJob().getEmployer() != null) {
                jobDto.put("companyName", sj.getJob().getEmployer().getCompanyName());
            }
            // For salary extraction
            if (sj.getJob().getSalaryRange() != null) {
                try {
                    String[] parts = sj.getJob().getSalaryRange().split("-");
                    if (parts.length == 2) {
                        jobDto.put("salaryMin", Integer.parseInt(parts[0].trim().replace(",", "")));
                        jobDto.put("salaryMax", Integer.parseInt(parts[1].trim().replace(",", "")));
                    }
                } catch (Exception e) {
                }
            }
            jobDto.put("experienceYearsMin", sj.getJob().getExperienceRequired());
            jobDto.put("jobType", sj.getJob().getJobType());
            dto.put("job", jobDto);
        }
        return dto;
    }

    @PostMapping("/{jobId}")
    public ResponseEntity<?> saveJob(@PathVariable("jobId") int jobId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean saved = savedJobService.saveJob(userDetails.getId(), jobId);
        if (saved) {
            return ResponseEntity.ok(Map.of("message", "Job saved successfully", "saved", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Job is already saved or failed to save"));
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> unsaveJob(@PathVariable("jobId") int jobId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean removed = savedJobService.unsaveJob(userDetails.getId(), jobId);
        if (removed) {
            return ResponseEntity.ok(Map.of("message", "Job unsaved successfully", "saved", false));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Job not found in saved list"));
    }

    @GetMapping
    public ResponseEntity<?> getSavedJobs(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<SavedJob> savedJobs = savedJobService.getSavedJobs(userDetails.getId());
        List<Map<String, Object>> content = savedJobs.stream().map(this::toSavedDto).collect(Collectors.toList());
        return ResponseEntity.ok(content);
    }

    @GetMapping("/{jobId}/check")
    public ResponseEntity<?> checkSavedStatus(@PathVariable("jobId") int jobId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean saved = savedJobService.isJobSaved(userDetails.getId(), jobId);
        return ResponseEntity.ok(Map.of("saved", saved));
    }
}

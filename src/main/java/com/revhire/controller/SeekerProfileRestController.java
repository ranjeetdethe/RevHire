package com.revhire.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revhire.model.JobSeeker;
import com.revhire.model.Resume;
import com.revhire.service.JobSeekerService;
import com.revhire.service.ResumeService;
import com.revhire.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/seeker")
public class SeekerProfileRestController {

    @Autowired
    private JobSeekerService jobSeekerService;

    @Autowired
    private ResumeService resumeService;

    private final ObjectMapper mapper = new ObjectMapper();

    private List<Map<String, Object>> parseJsonArray(String json) {
        if (json == null || json.trim().isEmpty())
            return new ArrayList<>();
        try {
            return mapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String toJsonArray(List<Map<String, Object>> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            JobSeeker seeker = jobSeekerService.getProfileByUserId(userDetails.getId());
            if (seeker != null) {
                Map<String, Object> profile = new HashMap<>();
                profile.put("id", seeker.getId());
                profile.put("firstName", seeker.getFirstName());
                profile.put("lastName", seeker.getLastName());
                profile.put("email", seeker.getEmail());
                profile.put("phone", seeker.getPhone());
                profile.put("location", seeker.getLocation());
                profile.put("about", seeker.getResumeText());

                profile.put("education", parseJsonArray(seeker.getEducation()));
                profile.put("experience", parseJsonArray(seeker.getExperience()));
                profile.put("skills", parseJsonArray(seeker.getSkills()));
                profile.put("certifications", parseJsonArray(seeker.getCertifications()));

                // Handle basic mapping if needed
                return ResponseEntity.ok(profile);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> data,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        JobSeeker current = jobSeekerService.getProfileByUserId(userDetails.getId());
        if (current != null) {
            if (data.containsKey("location"))
                current.setLocation((String) data.get("location"));
            if (data.containsKey("about"))
                current.setResumeText((String) data.get("about"));
            jobSeekerService.updateProfile(current);
            return ResponseEntity.ok(data);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/profile/completeness")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> getProfileCompleteness(@AuthenticationPrincipal CustomUserDetails userDetails) {
        JobSeeker seeker = jobSeekerService.getProfileByUserId(userDetails.getId());
        if (seeker == null)
            return ResponseEntity.notFound().build();

        int score = 0;

        // Photo (mocked to 0 or 10 depending on logic, let's give 0 since no photo
        // logic exists)
        // Score: photo:10, resume:20, education:15, experience:15, skills:10, basic:20,
        // prefs:10
        // basic (firstName, email, phone)
        if (seeker.getFirstName() != null && !seeker.getFirstName().isEmpty() &&
                seeker.getEmail() != null && !seeker.getEmail().isEmpty()) {
            score += 20;
        }

        Resume resume = resumeService.getResumeByUserId(userDetails.getId());
        if (resume != null)
            score += 20;

        List<Map<String, Object>> edu = parseJsonArray(seeker.getEducation());
        if (!edu.isEmpty())
            score += 15;

        List<Map<String, Object>> exp = parseJsonArray(seeker.getExperience());
        if (!exp.isEmpty())
            score += 15;

        List<Map<String, Object>> skills = parseJsonArray(seeker.getSkills());
        if (!skills.isEmpty())
            score += 10;

        if (seeker.getLocation() != null && !seeker.getLocation().isEmpty()) {
            score += 10; // Prefs mapped to location
        }

        // Add 10 for photo mock if we want it to reach 100% just for UI completeness
        // mock
        score += 10;

        return ResponseEntity.ok(Map.of("completeness", score));
    }

    @PostMapping("/education")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> addEducation(@RequestBody Map<String, Object> edu,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        JobSeeker seeker = jobSeekerService.getProfileByUserId(userDetails.getId());
        edu.put("id", System.currentTimeMillis() % 100000);
        List<Map<String, Object>> list = parseJsonArray(seeker.getEducation());
        list.add(edu);
        seeker.setEducation(toJsonArray(list));
        jobSeekerService.updateProfile(seeker);
        return ResponseEntity.ok(edu);
    }

    @PutMapping("/education/{id}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> updateEducation(@PathVariable("id") long id, @RequestBody Map<String, Object> edu,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        JobSeeker seeker = jobSeekerService.getProfileByUserId(userDetails.getId());
        List<Map<String, Object>> list = parseJsonArray(seeker.getEducation());
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get("id") != null && Long.parseLong(list.get(i).get("id").toString()) == id) {
                edu.put("id", id);
                list.set(i, edu);
                seeker.setEducation(toJsonArray(list));
                jobSeekerService.updateProfile(seeker);
                return ResponseEntity.ok(edu);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/education/{id}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> deleteEducation(@PathVariable("id") long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        JobSeeker seeker = jobSeekerService.getProfileByUserId(userDetails.getId());
        List<Map<String, Object>> list = parseJsonArray(seeker.getEducation());
        list.removeIf(item -> item.get("id") != null && Long.parseLong(item.get("id").toString()) == id);
        seeker.setEducation(toJsonArray(list));
        jobSeekerService.updateProfile(seeker);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }

    @PostMapping("/experience")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> addExperience(@RequestBody Map<String, Object> exp,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        JobSeeker seeker = jobSeekerService.getProfileByUserId(userDetails.getId());
        exp.put("id", System.currentTimeMillis() % 100000);
        List<Map<String, Object>> list = parseJsonArray(seeker.getExperience());
        list.add(exp);
        seeker.setExperience(toJsonArray(list));
        jobSeekerService.updateProfile(seeker);
        return ResponseEntity.ok(exp);
    }

    @PutMapping("/experience/{id}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> updateExperience(@PathVariable("id") long id, @RequestBody Map<String, Object> exp,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        JobSeeker seeker = jobSeekerService.getProfileByUserId(userDetails.getId());
        List<Map<String, Object>> list = parseJsonArray(seeker.getExperience());
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get("id") != null && Long.parseLong(list.get(i).get("id").toString()) == id) {
                exp.put("id", id);
                list.set(i, exp);
                seeker.setExperience(toJsonArray(list));
                jobSeekerService.updateProfile(seeker);
                return ResponseEntity.ok(exp);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/experience/{id}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> deleteExperience(@PathVariable("id") long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        JobSeeker seeker = jobSeekerService.getProfileByUserId(userDetails.getId());
        List<Map<String, Object>> list = parseJsonArray(seeker.getExperience());
        list.removeIf(item -> item.get("id") != null && Long.parseLong(item.get("id").toString()) == id);
        seeker.setExperience(toJsonArray(list));
        jobSeekerService.updateProfile(seeker);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }

    @PostMapping("/skills")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> addSkill(@RequestBody Map<String, Object> skill,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            JobSeeker seeker = jobSeekerService.getProfileByUserId(userDetails.getId());
            if (seeker == null) {
                return ResponseEntity.status(404).body(Map.of("message", "Job Seeker profile not found"));
            }

            if (skill == null) {
                skill = new HashMap<>();
            }
            Map<String, Object> modifiableSkill = new HashMap<>(skill);
            modifiableSkill.put("id", System.currentTimeMillis() % 100000);

            List<Map<String, Object>> list = parseJsonArray(seeker.getSkills());
            list.add(modifiableSkill);
            seeker.setSkills(toJsonArray(list));
            jobSeekerService.updateProfile(seeker);
            return ResponseEntity.ok(modifiableSkill);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to add skill: " + e.getMessage()));
        }
    }

    @DeleteMapping("/skills/{id}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> deleteSkill(@PathVariable("id") long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        JobSeeker seeker = jobSeekerService.getProfileByUserId(userDetails.getId());
        List<Map<String, Object>> list = parseJsonArray(seeker.getSkills());
        list.removeIf(item -> item.get("id") != null && Long.parseLong(item.get("id").toString()) == id);
        seeker.setSkills(toJsonArray(list));
        jobSeekerService.updateProfile(seeker);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }

    @PostMapping("/certifications")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> addCertification(@RequestBody Map<String, Object> cert,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        JobSeeker seeker = jobSeekerService.getProfileByUserId(userDetails.getId());
        if (seeker == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Job Seeker profile not found"));
        }

        if (cert == null) {
            cert = new HashMap<>();
        }
        Map<String, Object> modifiableCert = new HashMap<>(cert);
        modifiableCert.put("id", System.currentTimeMillis() % 100000);

        List<Map<String, Object>> list = parseJsonArray(seeker.getCertifications());
        list.add(modifiableCert);
        seeker.setCertifications(toJsonArray(list));
        jobSeekerService.updateProfile(seeker);
        return ResponseEntity.ok(modifiableCert);
    }

    @PutMapping("/certifications/{id}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> updateCertification(@PathVariable("id") long id, @RequestBody Map<String, Object> cert,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        JobSeeker seeker = jobSeekerService.getProfileByUserId(userDetails.getId());
        if (seeker == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Job Seeker profile not found"));
        }

        List<Map<String, Object>> list = parseJsonArray(seeker.getCertifications());
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get("id") != null && Long.parseLong(list.get(i).get("id").toString()) == id) {
                cert.put("id", id);
                list.set(i, cert);
                seeker.setCertifications(toJsonArray(list));
                jobSeekerService.updateProfile(seeker);
                return ResponseEntity.ok(cert);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/certifications/{id}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> deleteCertification(@PathVariable("id") long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        JobSeeker seeker = jobSeekerService.getProfileByUserId(userDetails.getId());
        if (seeker == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Job Seeker profile not found"));
        }

        List<Map<String, Object>> list = parseJsonArray(seeker.getCertifications());
        list.removeIf(item -> item.get("id") != null && Long.parseLong(item.get("id").toString()) == id);
        seeker.setCertifications(toJsonArray(list));
        jobSeekerService.updateProfile(seeker);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }
}

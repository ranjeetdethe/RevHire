package com.revhire.controller;

import com.revhire.model.Job;
import com.revhire.model.Employer;
import com.revhire.model.JobSeeker;
import com.revhire.service.JobService;
import com.revhire.service.JobSeekerService;
import com.revhire.repository.EmployerRepository;
import com.revhire.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobRestController {

    @Autowired
    private JobService jobService;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private JobSeekerService jobSeekerService;

    private Map<String, Object> toJobDTO(Job job) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", job.getId());
        dto.put("title", job.getTitle());
        dto.put("description", job.getDescription());
        dto.put("requiredSkills", job.getSkills());
        dto.put("experienceYearsMin", job.getExperienceRequired());
        dto.put("educationRequired", job.getEducationRequired());
        dto.put("location", job.getLocation());

        if (job.getSalaryRange() != null) {
            try {
                String[] parts = job.getSalaryRange().split("-");
                if (parts.length == 2) {
                    dto.put("salaryMin", Integer.parseInt(parts[0].trim().replace(",", "")));
                    dto.put("salaryMax", Integer.parseInt(parts[1].trim().replace(",", "")));
                } else if (parts.length == 1) {
                    dto.put("salaryMin", Integer.parseInt(parts[0].trim().replace(",", "")));
                }
            } catch (Exception ignored) {
            }
        }

        dto.put("jobType", job.getJobType());
        dto.put("status", job.getStatus());
        dto.put("deadline", job.getDeadline());
        dto.put("openingsCount", job.getNumberOfOpenings());
        if (job.getEmployer() != null) {
            dto.put("companyName", job.getEmployer().getCompanyName());
            dto.put("postedBy", job.getEmployer().getId());
            if (job.getEmployer().getUser() != null) {
                dto.put("postedByUserId", job.getEmployer().getUser().getId());
            }
        }
        dto.put("createdAt", job.getCreatedAt());
        return dto;
    }

    private Employer getCurrentEmployer() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            int userId = ((CustomUserDetails) principal).getUser().getId();
            return employerRepository.findByUser_Id(userId).orElse(null);
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<?> searchJobs(
            // Original AuraJobs / backend parameters
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "minExp", required = false) Integer minExp,
            @RequestParam(value = "maxExp", required = false) Integer maxExp,
            @RequestParam(value = "minSalary", required = false) Integer minSalary,
            @RequestParam(value = "maxSalary", required = false) Integer maxSalary,
            @RequestParam(value = "jobType", required = false) String jobType,
            @RequestParam(value = "workMode", required = false) String workMode,
            @RequestParam(value = "industry", required = false) String industry,

            // Angular RevHire frontend parameters (JobSearchParams)
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "experienceMin", required = false) Integer experienceMin,
            @RequestParam(value = "salaryMin", required = false) Integer salaryMin,
            @RequestParam(value = "salaryMax", required = false) Integer salaryMax,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "datePosted", required = false) String datePosted,
            @RequestParam(value = "sort", required = false) String sort,

            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        // Seamlessly support both the legacy query params and the new Angular ones.
        String effectiveKeyword = keyword != null && !keyword.isBlank()
                ? keyword
                : (title != null && !title.isBlank() ? title : null);

        Integer effectiveMinExp = minExp != null ? minExp : experienceMin;
        Integer effectiveMinSalary = minSalary != null ? minSalary : salaryMin;
        Integer effectiveMaxSalary = maxSalary != null ? maxSalary : salaryMax;

        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobsPage = jobService.getJobsWithFilters(
                effectiveKeyword, location, effectiveMinExp, maxExp, effectiveMinSalary, effectiveMaxSalary, jobType,
                workMode, industry, pageable);

        List<Map<String, Object>> content = jobsPage.getContent().stream()
                .map(this::toJobDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("totalElements", jobsPage.getTotalElements());
        response.put("totalPages", jobsPage.getTotalPages());
        response.put("number", jobsPage.getNumber());
        response.put("size", jobsPage.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<?> getJobById(@PathVariable("id") int id) {
        return jobService.getJobById(id)
                .map(job -> ResponseEntity.ok(toJobDTO(job)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/recommended")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> getRecommendedJobs() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            int userId = ((CustomUserDetails) principal).getUser().getId();
            JobSeeker seeker = jobSeekerService.getProfileByUserId(userId);
            if (seeker != null) {
                List<Job> recs = jobService.getRecommendedJobs(seeker.getSkills());
                List<Map<String, Object>> mapped = recs.stream().map(this::toJobDTO).collect(Collectors.toList());
                return ResponseEntity.ok(mapped);
            }
        }
        return ResponseEntity.ok(List.of()); // return empty if missing details
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> getMyJobs() {
        Employer emp = getCurrentEmployer();
        if (emp == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Employer identity not found."));
        }

        List<Job> myJobs = jobService.getJobsByEmployer(emp.getId());
        List<Map<String, Object>> content = myJobs.stream()
                .map(this::toJobDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("totalElements", content.size());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> createJob(@RequestBody Map<String, Object> payload) {
        Employer emp = getCurrentEmployer();
        if (emp == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Employer identity not found."));

        Job job = new Job();
        job.setEmployer(emp);
        applyPayload(job, payload);
        Job savedJob = jobService.postJob(job);
        return ResponseEntity.ok(toJobDTO(savedJob));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> updateJob(@PathVariable("id") int id, @RequestBody Map<String, Object> payload) {
        Employer emp = getCurrentEmployer();
        if (emp == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Employer identity not found."));

        Optional<Job> existingJobOpt = jobService.getJobById(id);
        if (existingJobOpt.isEmpty())
            return ResponseEntity.notFound().build();

        Job job = existingJobOpt.get();
        if (job.getEmployer() == null || job.getEmployer().getId() != emp.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You don't own this job."));
        }

        applyPayload(job, payload);
        jobService.updateJob(job);
        return ResponseEntity.ok(toJobDTO(job));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> updateJobStatus(@PathVariable("id") int id, @RequestBody Map<String, Object> payload) {
        Employer emp = getCurrentEmployer();
        if (emp == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Employer identity not found."));

        Optional<Job> existingJobOpt = jobService.getJobById(id);
        if (existingJobOpt.isEmpty())
            return ResponseEntity.notFound().build();

        Job job = existingJobOpt.get();
        if (job.getEmployer() == null || job.getEmployer().getId() != emp.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You don't own this job."));
        }

        if (payload.containsKey("status") && payload.get("status") != null) {
            try {
                job.setStatus(Job.JobStatus.valueOf(payload.get("status").toString()));
                jobService.updateJob(job);
                return ResponseEntity.ok(toJobDTO(job));
            } catch (Exception ignored) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid status value"));
            }
        }
        return ResponseEntity.badRequest().body(Map.of("message", "No status provided"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> deleteJob(@PathVariable("id") int id) {
        Employer emp = getCurrentEmployer();
        if (emp == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Employer identity not found."));

        Optional<Job> existingJobOpt = jobService.getJobById(id);
        if (existingJobOpt.isEmpty())
            return ResponseEntity.notFound().build();

        Job job = existingJobOpt.get();
        if (job.getEmployer() == null || job.getEmployer().getId() != emp.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You don't own this job."));
        }

        jobService.deleteJob(id);
        return ResponseEntity.ok(Map.of("message", "Job deleted successfully."));
    }

    private void applyPayload(Job job, Map<String, Object> payload) {
        if (payload.containsKey("title"))
            job.setTitle((String) payload.get("title"));
        if (payload.containsKey("description"))
            job.setDescription((String) payload.get("description"));
        if (payload.containsKey("requiredSkills"))
            job.setSkills((String) payload.get("requiredSkills"));
        if (payload.containsKey("educationRequired"))
            job.setEducationRequired((String) payload.get("educationRequired"));
        if (payload.containsKey("location"))
            job.setLocation((String) payload.get("location"));

        if (payload.containsKey("experienceYearsMin") && payload.get("experienceYearsMin") != null) {
            job.setExperienceRequired(Integer.parseInt(payload.get("experienceYearsMin").toString()));
        }

        if (payload.containsKey("openingsCount") && payload.get("openingsCount") != null) {
            job.setNumberOfOpenings(Integer.parseInt(payload.get("openingsCount").toString()));
        }

        String salaryMin = payload.containsKey("salaryMin") && payload.get("salaryMin") != null
                ? payload.get("salaryMin").toString()
                : "";
        String salaryMax = payload.containsKey("salaryMax") && payload.get("salaryMax") != null
                ? payload.get("salaryMax").toString()
                : "";
        if (!salaryMin.isEmpty() || !salaryMax.isEmpty()) {
            job.setSalaryRange(salaryMin + "-" + salaryMax);
        }

        if (payload.containsKey("jobType") && payload.get("jobType") != null) {
            try {
                job.setJobType(Job.JobType.valueOf(payload.get("jobType").toString()));
            } catch (Exception ignored) {
            }
        }

        if (payload.containsKey("status") && payload.get("status") != null) {
            try {
                job.setStatus(Job.JobStatus.valueOf(payload.get("status").toString()));
            } catch (Exception ignored) {
            }
        }

        if (payload.containsKey("deadline") && payload.get("deadline") != null) {
            try {
                String deadlineStr = payload.get("deadline").toString();
                if (!deadlineStr.isEmpty()) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    job.setDeadline(sdf.parse(deadlineStr));
                }
            } catch (Exception ignored) {
            }
        }
    }
}

package com.revhire.controller;

import com.revhire.dto.JobDTO;
import com.revhire.model.Employer;
import com.revhire.model.Job;
import com.revhire.model.User;
import com.revhire.model.Application;
import com.revhire.service.ApplicationService;
import com.revhire.service.JobService;
import com.revhire.service.UserService;
import com.revhire.service.EmployerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    private final JobService jobService;
    private final UserService userService;
    private final ApplicationService applicationService;
    private final EmployerService employerService;

    public EmployerController(JobService jobService, UserService userService,
            ApplicationService applicationService, EmployerService employerService) {
        this.jobService = jobService;
        this.userService = userService;
        this.applicationService = applicationService;
        this.employerService = employerService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal com.revhire.security.CustomUserDetails userDetails, Model model) {
        if (userDetails == null)
            return "redirect:/login";

        // No need to check role manually if SecurityConfig has
        // .requestMatchers("/employer/**").hasRole("EMPLOYER")

        Optional<Employer> employerOpt = userService.getEmployerProfile(userDetails.getId());
        if (employerOpt.isPresent()) {
            var jobs = jobService.getJobsByEmployer(employerOpt.get().getId());
            model.addAttribute("jobs", jobs);

            com.revhire.dto.DashboardStats stats = new com.revhire.dto.DashboardStats();
            stats.setTotalJobs(jobs.size());
            stats.setActiveJobs(jobs.stream().filter(j -> j.getStatus() == Job.JobStatus.OPEN).count());
            stats.setClosedJobs(jobs.stream().filter(j -> j.getStatus() == Job.JobStatus.CLOSED).count());
            model.addAttribute("stats", stats);
        } else {
            // If profile not found, maybe redirect to create profile
            return "redirect:/employer/profile";
        }

        return "employer/dashboard";
    }

    @GetMapping("/jobs/new")
    public String showPostJobForm(Model model) {
        model.addAttribute("jobDTO", new JobDTO());
        return "employer/post-job";
    }

    @PostMapping("/jobs")
    public String postJob(@Valid @ModelAttribute("jobDTO") JobDTO jobDTO,
            BindingResult result,
            @AuthenticationPrincipal com.revhire.security.CustomUserDetails userDetails,
            Model model) {
        if (result.hasErrors()) {
            return "employer/post-job";
        }

        Optional<Employer> employerOpt = userService.getEmployerProfile(userDetails.getId());

        if (employerOpt.isEmpty()) {
            return "redirect:/employer/profile";
        }

        Job job = new Job();
        job.setTitle(jobDTO.getTitle());
        job.setDescription(jobDTO.getDescription());
        job.setLocation(jobDTO.getLocation());
        job.setSalaryRange(jobDTO.getSalaryRange());
        job.setExperienceRequired(jobDTO.getExperienceRequired());
        job.setEmployer(employerOpt.get());
        job.setStatus(Job.JobStatus.OPEN);

        jobService.postJob(job);

        return "redirect:/employer/dashboard?success=posted";
    }

    @GetMapping("/jobs/{jobId}/applications")
    public String viewApplicants(@PathVariable int jobId,
            @AuthenticationPrincipal com.revhire.security.CustomUserDetails userDetails,
            Model model) {

        Optional<Job> job = jobService.getJobById(jobId);
        // Verify job ownership
        if (job.isEmpty() || job.get().getEmployer().getUser().getId() != userDetails.getId()) {
            return "redirect:/employer/dashboard";
        }

        model.addAttribute("job", job.get());
        model.addAttribute("applications", applicationService.getApplicationsByJob(jobId));

        return "employer/applicants";
    }

    @PostMapping("/applications/{appId}/status")
    public String updateApplicationStatus(@PathVariable int appId,
            @RequestParam("status") Application.ApplicationStatus status,
            @RequestParam(value = "notes", required = false) String notes,
            @AuthenticationPrincipal com.revhire.security.CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        // Service should handle ownership check ideally, but we pass employerId to be
        // safe
        if (applicationService.updateApplicationStatus(appId, userDetails.getId(), status, notes)) {
            redirectAttributes.addFlashAttribute("message", "Application status updated.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update status.");
        }

        return "redirect:/employer/dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal com.revhire.security.CustomUserDetails userDetails,
            Model model) {
        Employer profile = employerService.getProfileByUserId(userDetails.getId());
        model.addAttribute("profile", profile);
        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("activePage", "profile");
        return "employer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute Employer updatedProfile,
            @AuthenticationPrincipal com.revhire.security.CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Employer existingProfile = employerService.getProfileByUserId(userDetails.getId());
        if (existingProfile == null) {
            existingProfile = new Employer();
            existingProfile.setUser(userDetails.getUser());
        }

        // Ideally use DTO here to prevent mass assignment, but for now we manually set
        // fields
        existingProfile.setCompanyName(updatedProfile.getCompanyName());
        existingProfile.setDescription(updatedProfile.getDescription());
        existingProfile.setLocation(updatedProfile.getLocation());
        existingProfile.setWebsite(updatedProfile.getWebsite());
        existingProfile.setCompanySize(updatedProfile.getCompanySize());
        existingProfile.setIndustry(updatedProfile.getIndustry());

        employerService.updateProfile(existingProfile);

        redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
        return "redirect:/employer/profile";
    }
}

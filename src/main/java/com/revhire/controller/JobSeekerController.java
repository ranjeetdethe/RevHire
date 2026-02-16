package com.revhire.controller;

import com.revhire.dto.DashboardStats;
import com.revhire.model.Application;
import com.revhire.model.User;
import com.revhire.model.JobSeeker;
import com.revhire.model.SavedJob;
import com.revhire.service.ApplicationService;
import com.revhire.service.SavedJobService;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for Job Seeker Dashboard and Features
 * 
 * IMPORTANT: All filtering and calculation logic is done here,
 * NOT in Thymeleaf templates (Spring EL doesn't support lambdas)
 */
@Controller
@RequestMapping("/seeker")
public class JobSeekerController {

    private final ApplicationService applicationService;
    private final SavedJobService savedJobService;
    private final com.revhire.service.JobSeekerService jobSeekerService;

    public JobSeekerController(ApplicationService applicationService, SavedJobService savedJobService,
            com.revhire.service.JobSeekerService jobSeekerService) {
        this.applicationService = applicationService;
        this.savedJobService = savedJobService;
        this.jobSeekerService = jobSeekerService;
    }

    /**
     * Display job seeker dashboard with applications and statistics
     * 
     * MVC Architecture:
     * - Controller prepares ALL data
     * - Template only displays data
     * - No complex logic in Thymeleaf
     */
    @GetMapping("/saved-jobs")
    public String viewSavedJobs(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.JOB_SEEKER) {
            return "redirect:/login";
        }

        List<SavedJob> savedJobs = savedJobService.getSavedJobs(user.getId());
        model.addAttribute("savedJobs", savedJobs);
        // Add activePage for navbar highlighting
        model.addAttribute("activePage", "saved-jobs");

        return "seeker/saved-jobs";
    }

    @PostMapping("/saved-jobs/add")
    public String saveJob(@RequestParam("jobId") int jobId, HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.JOB_SEEKER) {
            return "redirect:/login";
        }

        if (savedJobService.saveJob(user.getId(), jobId)) {
            redirectAttributes.addFlashAttribute("message", "Job saved successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Job already saved or invalid.");
        }
        return "redirect:/jobs/" + jobId;
    }

    @PostMapping("/saved-jobs/remove")
    public String removeSavedJob(@RequestParam("jobId") int jobId, HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.JOB_SEEKER) {
            return "redirect:/login";
        }

        savedJobService.unsaveJob(user.getId(), jobId);
        redirectAttributes.addFlashAttribute("message", "Job removed from saved list.");
        return "redirect:/seeker/saved-jobs";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Get logged-in user from session
        User user = (User) session.getAttribute("user");

        // Security check
        if (user == null || user.getRole() != User.UserRole.JOB_SEEKER) {
            return "redirect:/login";
        }

        // Fetch applications for this user
        List<Application> applications = applicationService.getApplicationsByUserId(user.getId());

        // Calculate statistics using SERVICE layer (not template!)
        DashboardStats stats = applicationService.getApplicationStatsByUserId(user.getId());

        // Pass simple, pre-calculated data to template
        model.addAttribute("applications", applications);
        model.addAttribute("stats", stats);
        model.addAttribute("user", user);

        return "seeker/dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.JOB_SEEKER) {
            return "redirect:/login";
        }

        JobSeeker profile = jobSeekerService.getProfileByUserId(user.getId());
        model.addAttribute("profile", profile);
        model.addAttribute("user", user);
        model.addAttribute("activePage", "profile");
        return "seeker/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute JobSeeker updatedProfile, HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.JOB_SEEKER) {
            return "redirect:/login";
        }

        JobSeeker existingProfile = jobSeekerService.getProfileByUserId(user.getId());
        if (existingProfile == null) {
            existingProfile = new JobSeeker();
            existingProfile.setUser(user);
        }

        existingProfile.setResumeText(updatedProfile.getResumeText());
        existingProfile.setEducation(updatedProfile.getEducation());
        existingProfile.setExperience(updatedProfile.getExperience());
        existingProfile.setSkills(updatedProfile.getSkills());
        existingProfile.setCertifications(updatedProfile.getCertifications());
        existingProfile.setLocation(updatedProfile.getLocation());

        jobSeekerService.updateProfile(existingProfile);

        redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
        return "redirect:/seeker/profile";
    }
}

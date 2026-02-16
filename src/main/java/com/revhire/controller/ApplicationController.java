package com.revhire.controller;

import com.revhire.model.Application;
import com.revhire.model.User;
import com.revhire.service.ApplicationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/apply")
    public String applyForJob(@RequestParam("jobId") int jobId,
            @RequestParam(value = "coverLetter", required = false) String coverLetter,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.JOB_SEEKER) {
            return "redirect:/login"; // Or handle unauthorized access
        }

        try {
            applicationService.applyJob(jobId, user.getId(), coverLetter);
            redirectAttributes.addFlashAttribute("message", "Successfully applied for the job!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to apply. " + e.getMessage());
        }

        return "redirect:/jobs/" + jobId;
    }

    @GetMapping("/my-applications")
    public String viewMyApplications(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.JOB_SEEKER) {
            return "redirect:/login";
        }

        List<Application> applications = applicationService.getApplicationsByUserId(user.getId());
        model.addAttribute("applications", applications);
        return "my-applications";
    }

    @PostMapping("/{appId}/withdraw")
    public String withdrawApplication(@PathVariable int appId,
            @RequestParam(value = "reason", required = false) String reason,
            HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.JOB_SEEKER) {
            return "redirect:/login";
        }

        if (applicationService.withdrawApplication(appId, reason)) {
            redirectAttributes.addFlashAttribute("message", "Application withdrawn.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to withdraw application.");
        }

        return "redirect:/applications/my-applications";
    }
}

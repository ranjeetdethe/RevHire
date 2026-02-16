package com.revhire.controller;

import com.revhire.model.Job;
import com.revhire.model.User;
import com.revhire.service.JobService;
import com.revhire.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Admin Controller for RevHire Platform Orchestration
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final JobService jobService;

    public AdminController(UserService userService, JobService jobService) {
        this.userService = userService;
        this.jobService = jobService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<User> users = userService.getAllUsers();
        List<Job> jobs = jobService.getAllJobs();

        model.addAttribute("users", users);
        model.addAttribute("jobs", jobs);
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("totalJobs", jobs.size());

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/jobs")
    public String viewAllJobs(Model model) {
        List<Job> jobs = jobService.getAllJobs();
        model.addAttribute("jobs", jobs);
        return "admin/jobs";
    }
}

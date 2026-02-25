package com.revhire.controller;

import com.revhire.model.Job;
import com.revhire.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Handles search requests for jobs with multiple filter criteria.
     * Uses AuraJobs search engine logic.
     */
    @GetMapping
    public String discoverJobs(@RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "jobType", required = false) Job.JobType jobType,
            @RequestParam(value = "experience", required = false) Integer experience,
            Model uiModel) {

        List<Job> jobResults;

        // Check if any filters are applied
        boolean isFiltering = (keyword != null && !keyword.trim().isEmpty()) ||
                (location != null && !location.trim().isEmpty()) ||
                jobType != null || experience != null;

        if (isFiltering) {
            // Apply advanced search filters
            jobResults = jobService.searchJobs(keyword, location, jobType, experience);

            // Persist filter state to UI
            uiModel.addAttribute("keyword", keyword);
            uiModel.addAttribute("location", location);
            uiModel.addAttribute("jobType", jobType);
            uiModel.addAttribute("experience", experience);
        } else {
            // Default view: Show all open positions
            jobResults = jobService.getAllJobs();
        }

        uiModel.addAttribute("jobs", jobResults);
        uiModel.addAttribute("jobTypes", Job.JobType.values());
        uiModel.addAttribute("activePage", "jobs");
        return "jobs";
    }

    @GetMapping("/{id}")
    public String viewJobDetails(@PathVariable("id") int id, Model model) {
        Optional<Job> jobMatch = jobService.getJobById(id);

        if (jobMatch.isPresent()) {
            model.addAttribute("job", jobMatch.get());
            // Record view stats could go here in future AuraJobs update
            return "job-details";
        }

        // Handle 404 case
        model.addAttribute("error", "The requested job posting is no longer available.");
        return "error";
    }
}

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

    @GetMapping
    public String getAllJobs(@RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "jobType", required = false) Job.JobType jobType,
            @RequestParam(value = "experience", required = false) Integer experience,
            Model model) {

        List<Job> jobs;
        if ((keyword != null && !keyword.trim().isEmpty()) ||
                (location != null && !location.trim().isEmpty()) ||
                jobType != null || experience != null) {

            jobs = jobService.searchJobs(keyword, location, jobType, experience);

            model.addAttribute("keyword", keyword);
            model.addAttribute("location", location);
            model.addAttribute("jobType", jobType);
            model.addAttribute("experience", experience);
        } else {
            jobs = jobService.getAllJobs();
        }
        model.addAttribute("jobs", jobs);
        model.addAttribute("jobTypes", Job.JobType.values());
        model.addAttribute("activePage", "jobs");
        return "jobs";
    }

    @GetMapping("/{id}")
    public String getJobById(@PathVariable("id") int id, Model model) {
        Optional<Job> job = jobService.getJobById(id);
        if (job.isPresent()) {
            model.addAttribute("job", job.get());
            return "job-details";
        }
        model.addAttribute("error", "Job not found");
        return "error";
    }
}

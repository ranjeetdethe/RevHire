package com.revhire.service;

import com.revhire.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface JobService {
    Job postJob(Job job);

    Optional<Job> getJobById(int id);

    List<Job> getAllJobs();

    List<Job> getJobsByEmployer(int employerId);

    boolean updateJob(Job job);

    boolean deleteJob(int id);

    List<Job> searchJobs(String keyword);

    List<Job> searchJobs(String keyword, String location);

    List<Job> searchJobs(String keyword, String location, Job.JobType jobType, Integer experience);

    Page<Job> getJobsWithFilters(String keyword, String location, Integer minExp, Integer maxExp,
            Integer minSalary, Integer maxSalary, String jobType, String workMode,
            String industry, Pageable pageable);

    List<Job> getRecommendedJobs(String skills);
}

package com.revhire.service.impl;

import com.revhire.model.Job;
import com.revhire.repository.JobRepository;
import com.revhire.service.JobService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public Job postJob(Job job) {
        if (job.getCreatedAt() == null) {
            job.setCreatedAt(new java.util.Date());
        }
        // Set default status if null
        if (job.getStatus() == null) {
            job.setStatus(Job.JobStatus.OPEN);
        }
        return jobRepository.save(job);
    }

    @Override
    public Optional<Job> getJobById(int id) {
        return jobRepository.findById(id);
    }

    @Override
    public List<Job> getAllJobs() {
        return jobRepository.findByStatusWithEmployer(Job.JobStatus.OPEN);
    }

    @Override
    public List<Job> getJobsByEmployer(int employerId) {
        return jobRepository.findByEmployer_Id(employerId);
    }

    @Override
    public boolean updateJob(Job job) {
        if (jobRepository.existsById(job.getId())) {
            // Need to ensure createdAt isn't lost if not passed in update
            Optional<Job> existing = jobRepository.findById(job.getId());
            if (existing.isPresent()) {
                Job existingJob = existing.get();
                if (job.getCreatedAt() == null) {
                    job.setCreatedAt(existingJob.getCreatedAt());
                }
                // Also employerId shouldn't change ideally, but for now just save
            }
            jobRepository.save(job);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteJob(int id) {
        if (jobRepository.existsById(id)) {
            jobRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Job> searchJobs(String keyword) {
        return jobRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public List<Job> searchJobs(String keyword, String location) {
        if (location == null || location.trim().isEmpty()) {
            return searchJobs(keyword);
        }
        return jobRepository.searchJobs(keyword, location);
    }

    @Override
    public List<Job> searchJobs(String keyword, String location, Job.JobType jobType, Integer experience) {
        String k = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String l = (location != null && !location.trim().isEmpty()) ? location.trim() : null;
        return jobRepository.advancedSearch(k, l, jobType, experience);
    }
}

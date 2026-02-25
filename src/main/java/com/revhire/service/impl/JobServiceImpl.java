package com.revhire.service.impl;

import com.revhire.model.Job;
import com.revhire.repository.JobRepository;
import com.revhire.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@SuppressWarnings("null")
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
            Optional<Job> existing = jobRepository.findById(job.getId());
            if (existing.isPresent()) {
                Job existingJob = existing.get();
                if (job.getCreatedAt() == null) {
                    job.setCreatedAt(existingJob.getCreatedAt());
                }
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

    @Override
    public Page<Job> getJobsWithFilters(String keyword, String location, Integer minExp, Integer maxExp,
            Integer minSalary, Integer maxSalary, String jobType, String workMode,
            String industry, Pageable pageable) {
        Specification<Job> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), Job.JobStatus.OPEN));

            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKw = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), likeKw),
                        cb.like(cb.lower(root.get("description")), likeKw),
                        cb.like(cb.lower(root.get("skills")), likeKw)));
            }

            if (location != null && !location.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }

            if (minExp != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("experienceRequired"), minExp));
            }
            if (maxExp != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("experienceRequired"), maxExp));
            }

            if (jobType != null && !jobType.trim().isEmpty()) {
                try {
                    Job.JobType type = Job.JobType.valueOf(jobType.toUpperCase());
                    predicates.add(cb.equal(root.get("jobType"), type));
                } catch (IllegalArgumentException ignored) {
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return jobRepository.findAll(spec, pageable);
    }

    @Override
    public List<Job> getRecommendedJobs(String skills) {
        // Mock recommendation: find jobs matching any of the skills
        String[] skillArray = skills != null ? skills.split(",") : new String[0];

        Specification<Job> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), Job.JobStatus.OPEN));

            if (skillArray.length > 0) {
                List<Predicate> skillPredicates = new ArrayList<>();
                for (String s : skillArray) {
                    String trimmed = s.trim().toLowerCase();
                    if (!trimmed.isEmpty()) {
                        skillPredicates.add(cb.like(cb.lower(root.get("skills")), "%" + trimmed + "%"));
                        skillPredicates.add(cb.like(cb.lower(root.get("description")), "%" + trimmed + "%"));
                    }
                }
                if (!skillPredicates.isEmpty()) {
                    predicates.add(cb.or(skillPredicates.toArray(new Predicate[0])));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Return top 10 recommended
        Page<Job> found = jobRepository.findAll(spec, org.springframework.data.domain.PageRequest.of(0, 10));
        return found.getContent();
    }
}

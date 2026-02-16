package com.revhire.service.impl;

import com.revhire.service.NotificationService;

import com.revhire.dto.DashboardStats;
import com.revhire.model.Application;
import com.revhire.model.Application.ApplicationStatus;
import com.revhire.model.Job;
import com.revhire.model.JobSeeker;

import com.revhire.repository.ApplicationRepository;
import com.revhire.repository.JobRepository;
import com.revhire.repository.JobSeekerRepository;

import com.revhire.service.ApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final JobSeekerRepository jobSeekerRepository;

    private final NotificationService notificationService;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository,
            JobRepository jobRepository,
            JobSeekerRepository jobSeekerRepository,
            NotificationService notificationService) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.jobSeekerRepository = jobSeekerRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void applyJob(int jobId, int userId) {
        applyJob(jobId, userId, null);
    }

    @Override
    public void applyJob(int jobId, int userId, String coverLetter) {
        // Check if already applied
        Optional<JobSeeker> seekerOpt = jobSeekerRepository.findByUser_Id(userId);
        if (seekerOpt.isEmpty()) {
            throw new IllegalArgumentException("Job Seeker profile not found. Please complete your profile first.");
        }

        JobSeeker seeker = seekerOpt.get();

        if (applicationRepository.existsByJob_IdAndJobSeeker_Id(jobId, seeker.getId())) {
            throw new IllegalArgumentException("Already applied to this job");
        }

        Optional<Job> jobOpt = jobRepository.findById(jobId);
        if (jobOpt.isEmpty()) {
            throw new IllegalArgumentException("Job not found");
        }

        Job job = jobOpt.get();

        Application application = new Application(0, job, seeker, ApplicationStatus.APPLIED, new Date(), coverLetter);
        applicationRepository.save(application);

        // Notify Employer
        String message = "New application from " + seeker.getUser().getFirstName() + " for job: " + job.getTitle();
        notificationService.createNotification(job.getEmployer().getUser().getId(), message);
    }

    @Override
    public List<Application> getApplicationsByJob(int jobId) {
        return applicationRepository.findByJob_Id(jobId);
    }

    @Override
    public List<Application> getApplicationsByUserId(int userId) {
        // Use the updated repository method that queries via JobSeeker->User
        // relationship
        return applicationRepository.findByJobSeeker_User_Id(userId);
    }

    @Override
    public boolean updateApplicationStatus(int applicationId, int employerId, ApplicationStatus status) {
        return updateApplicationStatus(applicationId, employerId, status, null);
    }

    @Override
    public boolean updateApplicationStatus(int applicationId, int employerId, ApplicationStatus status, String notes) {
        Optional<Application> appOpt = applicationRepository.findById(applicationId);
        if (appOpt.isPresent()) {
            Application app = appOpt.get();

            // Security Check: Ensure the job belongs to the logged-in employer
            if (app.getJob().getEmployer().getUser().getId() != employerId) {
                return false;
            }

            app.setStatus(status);
            if (notes != null && !notes.trim().isEmpty()) {
                app.setEmployerNotes(notes);
            }
            applicationRepository.save(app);

            // Notify Job Seeker
            String message = "Your application for " + app.getJob().getTitle() + " at " +
                    app.getJob().getEmployer().getCompanyName() + " has been updated to " + status;
            notificationService.createNotification(app.getJobSeeker().getUser().getId(), message);

            return true;
        }
        return false;
    }

    @Override
    public boolean withdrawApplication(int applicationId) {
        return withdrawApplication(applicationId, "No reason provided");
    }

    @Override
    public boolean withdrawApplication(int applicationId, String reason) {
        Optional<Application> appOpt = applicationRepository.findById(applicationId);
        if (appOpt.isPresent()) {
            Application app = appOpt.get();
            app.setStatus(ApplicationStatus.WITHDRAWN);
            app.setWithdrawalReason(reason);
            applicationRepository.save(app);
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Integer> getJobStatistics(int jobId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("APPLIED", applicationRepository.countByJob_IdAndStatus(jobId, ApplicationStatus.APPLIED));
        stats.put("SHORTLISTED", applicationRepository.countByJob_IdAndStatus(jobId, ApplicationStatus.SHORTLISTED));
        stats.put("REJECTED", applicationRepository.countByJob_IdAndStatus(jobId, ApplicationStatus.REJECTED));
        return stats;
    }

    @Override
    public DashboardStats getApplicationStatsByUserId(int userId) {
        DashboardStats stats = new DashboardStats();
        List<Application> applications = getApplicationsByUserId(userId);

        stats.setTotalApplications(applications.size());
        stats.setAppliedCount(applications.stream().filter(a -> a.getStatus() == ApplicationStatus.APPLIED).count());
        stats.setShortlistedCount(
                applications.stream().filter(a -> a.getStatus() == ApplicationStatus.SHORTLISTED).count());
        stats.setRejectedCount(applications.stream().filter(a -> a.getStatus() == ApplicationStatus.REJECTED).count());
        stats.setWithdrawnCount(
                applications.stream().filter(a -> a.getStatus() == ApplicationStatus.WITHDRAWN).count());

        return stats;
    }
}

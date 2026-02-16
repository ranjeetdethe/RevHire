package com.revhire.service;

import com.revhire.dto.DashboardStats;
import com.revhire.model.Application;
import java.util.List;
import java.util.Map;

public interface ApplicationService {
    void applyJob(int jobId, int seekerId);

    void applyJob(int jobId, int seekerId, String coverLetter);

    List<Application> getApplicationsByJob(int jobId);

    List<Application> getApplicationsByUserId(int userId);

    // Dashboard statistics
    DashboardStats getApplicationStatsByUserId(int userId);

    // New features
    boolean withdrawApplication(int applicationId);

    boolean withdrawApplication(int applicationId, String reason);

    Map<String, Integer> getJobStatistics(int jobId);

    boolean updateApplicationStatus(int applicationId, int employerId, Application.ApplicationStatus status);

    boolean updateApplicationStatus(int applicationId, int employerId, Application.ApplicationStatus status,
            String notes);
}

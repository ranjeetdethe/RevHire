package com.revhire.service;

import com.revhire.model.Application;
import java.util.List;

public interface ApplicationService {
    Application applyJob(int jobId, int seekerId);

    List<Application> getApplicationsByJob(int jobId);

    List<Application> getApplicationsBySeeker(int seekerId);

    // New features
    boolean withdrawApplication(int applicationId);

    java.util.Map<String, Integer> getJobStatistics(int jobId);

    boolean updateApplicationStatus(int applicationId, Application.ApplicationStatus status);
}

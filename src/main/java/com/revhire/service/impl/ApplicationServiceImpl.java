package com.revhire.service.impl;

import com.revhire.dao.ApplicationDAO;
import com.revhire.dao.impl.ApplicationDAOImpl;
import com.revhire.model.Application;
import com.revhire.service.ApplicationService;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationDAO applicationDAO;

    public ApplicationServiceImpl() {
        this.applicationDAO = new ApplicationDAOImpl();
    }

    @Override
    public Application applyJob(int jobId, int seekerId) {
        // Check if already applied (handled by DB constraint, but good to check here
        // too or handle exception)
        Application application = new Application();
        application.setJobId(jobId);
        application.setSeekerId(seekerId);
        application.setStatus(Application.ApplicationStatus.APPLIED);
        return applicationDAO.apply(application);
    }

    @Override
    public List<Application> getApplicationsByJob(int jobId) {
        return applicationDAO.findByJobId(jobId);
    }

    @Override
    public List<Application> getApplicationsBySeeker(int seekerId) {
        return applicationDAO.findBySeekerId(seekerId);
    }

    @Override
    public boolean updateApplicationStatus(int applicationId, Application.ApplicationStatus status) {
        return applicationDAO.updateStatus(applicationId, status);
    }

    @Override
    public boolean withdrawApplication(int applicationId) {
        return applicationDAO.updateStatus(applicationId, Application.ApplicationStatus.WITHDRAWN);
    }

    @Override
    public Map<String, Integer> getJobStatistics(int jobId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("APPLIED", applicationDAO.countByJobAndStatus(jobId, Application.ApplicationStatus.APPLIED));
        stats.put("SHORTLISTED", applicationDAO.countByJobAndStatus(jobId, Application.ApplicationStatus.SHORTLISTED));
        stats.put("REJECTED", applicationDAO.countByJobAndStatus(jobId, Application.ApplicationStatus.REJECTED));
        return stats;
    }
}

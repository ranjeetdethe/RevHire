package com.revhire.service.impl;

import com.revhire.dao.JobDAO;
import com.revhire.dao.impl.JobDAOImpl;
import com.revhire.model.Job;
import com.revhire.service.JobService;

import java.util.List;
import java.util.Optional;

public class JobServiceImpl implements JobService {

    private final JobDAO jobDAO;

    public JobServiceImpl() {
        this.jobDAO = new JobDAOImpl();
    }

    @Override
    public Job postJob(Job job) {
        return jobDAO.createJob(job);
    }

    @Override
    public Optional<Job> getJobById(int id) {
        return jobDAO.findById(id);
    }

    @Override
    public List<Job> getAllJobs() {
        return jobDAO.findAll();
    }

    @Override
    public List<Job> getJobsByEmployer(int employerId) {
        return jobDAO.findByEmployerId(employerId);
    }

    @Override
    public boolean updateJob(Job job) {
        return jobDAO.updateJob(job);
    }

    @Override
    public boolean deleteJob(int id) {
        return jobDAO.deleteJob(id);
    }

    @Override
    public List<Job> searchJobs(String keyword) {
        return jobDAO.searchJobs(keyword);
    }
}

package com.revhire.dao;

import com.revhire.model.Job;
import java.util.List;
import java.util.Optional;

public interface JobDAO {
    Job createJob(Job job);

    Optional<Job> findById(int id);

    List<Job> findAll();

    List<Job> findByEmployerId(int employerId);

    boolean updateJob(Job job);

    boolean deleteJob(int id);

    List<Job> searchJobs(String keyword);
}

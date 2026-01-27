package com.revhire.dao;

import com.revhire.model.JobSeeker;
import java.util.Optional;

public interface JobSeekerDAO {
    void create(JobSeeker jobSeeker);

    Optional<JobSeeker> findByUserId(int userId);

    boolean update(JobSeeker jobSeeker);
}

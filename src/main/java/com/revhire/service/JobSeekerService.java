package com.revhire.service;

import com.revhire.model.JobSeeker;

public interface JobSeekerService {
    JobSeeker getProfileByUserId(int userId);

    JobSeeker updateProfile(JobSeeker jobSeeker);
}

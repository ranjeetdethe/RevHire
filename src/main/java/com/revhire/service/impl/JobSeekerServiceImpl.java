package com.revhire.service.impl;

import com.revhire.model.JobSeeker;
import com.revhire.repository.JobSeekerRepository;
import com.revhire.service.JobSeekerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobSeekerServiceImpl implements JobSeekerService {

    private final JobSeekerRepository jobSeekerRepository;

    public JobSeekerServiceImpl(JobSeekerRepository jobSeekerRepository) {
        this.jobSeekerRepository = jobSeekerRepository;
    }

    @Override
    public JobSeeker getProfileByUserId(int userId) {
        return jobSeekerRepository.findByUser_Id(userId).orElse(null);
    }

    @Override
    public JobSeeker updateProfile(JobSeeker jobSeeker) {
        return jobSeekerRepository.save(jobSeeker);
    }
}

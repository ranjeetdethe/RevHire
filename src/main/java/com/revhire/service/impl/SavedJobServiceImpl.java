package com.revhire.service.impl;

import com.revhire.model.Job;
import com.revhire.model.JobSeeker;
import com.revhire.model.SavedJob;
import com.revhire.repository.JobRepository;
import com.revhire.repository.JobSeekerRepository;
import com.revhire.repository.SavedJobRepository;
import com.revhire.service.SavedJobService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SavedJobServiceImpl implements SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final JobRepository jobRepository;

    public SavedJobServiceImpl(SavedJobRepository savedJobRepository, JobSeekerRepository jobSeekerRepository,
            JobRepository jobRepository) {
        this.savedJobRepository = savedJobRepository;
        this.jobSeekerRepository = jobSeekerRepository;
        this.jobRepository = jobRepository;
    }

    @Override
    public boolean saveJob(int userId, int jobId) {
        Optional<JobSeeker> seekerOpt = jobSeekerRepository.findByUser_Id(userId);
        Optional<Job> jobOpt = jobRepository.findById(jobId);

        if (seekerOpt.isPresent() && jobOpt.isPresent()) {
            if (savedJobRepository.existsByJobSeeker_IdAndJob_Id(seekerOpt.get().getId(), jobId)) {
                return false; // Already saved
            }
            SavedJob savedJob = new SavedJob(seekerOpt.get(), jobOpt.get());
            savedJobRepository.save(savedJob);
            return true;
        }
        return false;
    }

    @Override
    public boolean unsaveJob(int userId, int jobId) {
        Optional<JobSeeker> seekerOpt = jobSeekerRepository.findByUser_Id(userId);
        if (seekerOpt.isPresent()) {
            Optional<SavedJob> savedJob = savedJobRepository.findByJobSeeker_IdAndJob_Id(seekerOpt.get().getId(),
                    jobId);
            if (savedJob.isPresent()) {
                savedJobRepository.delete(savedJob.get());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isJobSaved(int userId, int jobId) {
        Optional<JobSeeker> seekerOpt = jobSeekerRepository.findByUser_Id(userId);
        return seekerOpt.isPresent()
                && savedJobRepository.existsByJobSeeker_IdAndJob_Id(seekerOpt.get().getId(), jobId);
    }

    @Override
    public List<SavedJob> getSavedJobs(int userId) {
        Optional<JobSeeker> seekerOpt = jobSeekerRepository.findByUser_Id(userId);
        return seekerOpt.map(jobSeeker -> savedJobRepository.findByJobSeeker_Id(jobSeeker.getId()))
                .orElse(List.of());
    }
}

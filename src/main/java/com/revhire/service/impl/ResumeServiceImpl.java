package com.revhire.service.impl;

import com.revhire.model.JobSeeker;
import com.revhire.model.Resume;
import com.revhire.repository.JobSeekerRepository;
import com.revhire.repository.ResumeRepository;
import com.revhire.service.ResumeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@Transactional
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final JobSeekerRepository jobSeekerRepository;

    public ResumeServiceImpl(ResumeRepository resumeRepository, JobSeekerRepository jobSeekerRepository) {
        this.resumeRepository = resumeRepository;
        this.jobSeekerRepository = jobSeekerRepository;
    }

    @Override
    public Resume getResumeByUserId(int userId) {
        Optional<JobSeeker> seekerOpt = jobSeekerRepository.findByUser_Id(userId);
        return seekerOpt.map(jobSeeker -> resumeRepository.findByJobSeekerId(jobSeeker.getId()).orElse(null))
                .orElse(null);
    }

    @Override
    public void saveResumeFile(int userId, MultipartFile file) throws IOException {
        Optional<JobSeeker> seekerOpt = jobSeekerRepository.findByUser_Id(userId);
        if (seekerOpt.isEmpty()) {
            // In a better architecture, we might auto-create the profile here or throw a
            // specific exception
            throw new IllegalArgumentException("Job Seeker profile not found for user ID: " + userId);
        }

        int jobSeekerId = seekerOpt.get().getId();

        Optional<Resume> existingOpt = resumeRepository.findByJobSeekerId(jobSeekerId);
        Resume resume;
        if (existingOpt.isPresent()) {
            resume = existingOpt.get();
        } else {
            resume = new Resume();
            resume.setJobSeekerId(jobSeekerId);
        }

        resume.setData(file.getBytes());
        resume.setFileName(file.getOriginalFilename());
        resume.setFileType(file.getContentType());
        if (resume.getSummary() == null) {
            resume.setSummary("Uploaded Resume: " + file.getOriginalFilename());
        }

        resumeRepository.save(resume);
    }
}

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
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
@SuppressWarnings("null")
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
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/pdf") && !contentType.equals("application/msword")
                && !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new IllegalArgumentException("Only PDF or DOC/DOCX files are supported");
        }

        if (file.getSize() > 5 * 1024 * 1024) { // 5MB Limit enforcement programmatically or let tomcat handle 10mb
                                                // limit
            throw new IllegalArgumentException("File size must be less than 5MB");
        }

        Optional<JobSeeker> seekerOpt = jobSeekerRepository.findByUser_Id(userId);
        if (seekerOpt.isEmpty()) {
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
            resume.setCreatedAt(new Date());
        }

        resume.setData(file.getBytes());
        resume.setFileName(file.getOriginalFilename());
        resume.setFileType(contentType);
        if (resume.getSummary() == null) {
            resume.setSummary("Uploaded Resume: " + file.getOriginalFilename());
        }
        resume.setUpdatedAt(new Date());

        resumeRepository.save(resume);
    }

    @Override
    public boolean deleteResume(int userId) {
        Optional<JobSeeker> seekerOpt = jobSeekerRepository.findByUser_Id(userId);
        if (seekerOpt.isPresent()) {
            Optional<Resume> existingOpt = resumeRepository.findByJobSeekerId(seekerOpt.get().getId());
            if (existingOpt.isPresent()) {
                resumeRepository.delete(existingOpt.get());
                return true;
            }
        }
        return false;
    }
}

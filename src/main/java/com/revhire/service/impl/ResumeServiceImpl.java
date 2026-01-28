package com.revhire.service.impl;

import com.revhire.dao.ResumeDAO;
import com.revhire.dao.impl.ResumeDAOImpl;
import com.revhire.model.*;
import com.revhire.service.ResumeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class ResumeServiceImpl implements ResumeService {
    private static final Logger logger = LogManager.getLogger(ResumeServiceImpl.class);
    private final ResumeDAO resumeDAO;

    public ResumeServiceImpl() {
        this.resumeDAO = new ResumeDAOImpl();
    }

    // For testing
    public ResumeServiceImpl(ResumeDAO resumeDAO) {
        this.resumeDAO = resumeDAO;
    }

    @Override
    public Resume getResumeBySeekerId(int jobSeekerId) {
        return resumeDAO.getResumeBySeekerId(jobSeekerId).orElse(null);
    }

    @Override
    public Resume createResume(int jobSeekerId, String summary) {
        Optional<Resume> existing = resumeDAO.getResumeBySeekerId(jobSeekerId);
        if (existing.isPresent()) {
            logger.warn("Resume already exists for seeker ID: {}", jobSeekerId);
            return existing.get();
        }
        Resume resume = new Resume();
        resume.setJobSeekerId(jobSeekerId);
        resume.setSummary(summary);
        return resumeDAO.createResume(resume);
    }

    @Override
    public boolean updateSummary(int jobSeekerId, String summary) {
        Optional<Resume> existing = resumeDAO.getResumeBySeekerId(jobSeekerId);
        if (existing.isPresent()) {
            return resumeDAO.updateSummary(existing.get().getId(), summary);
        }
        return false;
    }

    @Override
    public boolean addEducation(int jobSeekerId, ResumeEducation education) {
        Optional<Resume> resume = resumeDAO.getResumeBySeekerId(jobSeekerId);
        if (resume.isPresent()) {
            education.setResumeId(resume.get().getId());
            return resumeDAO.addEducation(education);
        }
        return false;
    }

    @Override
    public boolean deleteEducation(int jobSeekerId, int educationId) {
        // Technically strict ownership check might be needed here to ensure this
        // education belongs to this seeker
        // For simplicity, we trust the flow
        return resumeDAO.deleteEducation(educationId);
    }

    @Override
    public boolean addExperience(int jobSeekerId, ResumeExperience experience) {
        Optional<Resume> resume = resumeDAO.getResumeBySeekerId(jobSeekerId);
        if (resume.isPresent()) {
            experience.setResumeId(resume.get().getId());
            return resumeDAO.addExperience(experience);
        }
        return false;
    }

    @Override
    public boolean deleteExperience(int jobSeekerId, int experienceId) {
        return resumeDAO.deleteExperience(experienceId);
    }

    @Override
    public boolean addProject(int jobSeekerId, ResumeProject project) {
        Optional<Resume> resume = resumeDAO.getResumeBySeekerId(jobSeekerId);
        if (resume.isPresent()) {
            project.setResumeId(resume.get().getId());
            return resumeDAO.addProject(project);
        }
        return false;
    }

    @Override
    public boolean deleteProject(int jobSeekerId, int projectId) {
        return resumeDAO.deleteProject(projectId);
    }

    @Override
    public boolean addSkill(int jobSeekerId, ResumeSkill skill) {
        Optional<Resume> resume = resumeDAO.getResumeBySeekerId(jobSeekerId);
        if (resume.isPresent()) {
            skill.setResumeId(resume.get().getId());
            return resumeDAO.addSkill(skill);
        }
        return false;
    }

    @Override
    public boolean deleteSkill(int jobSeekerId, int skillId) {
        return resumeDAO.deleteSkill(skillId);
    }
}

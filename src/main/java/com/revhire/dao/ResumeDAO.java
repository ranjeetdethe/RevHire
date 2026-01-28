package com.revhire.dao;

import com.revhire.model.Resume;
import com.revhire.model.ResumeEducation;
import com.revhire.model.ResumeExperience;
import com.revhire.model.ResumeProject;
import com.revhire.model.ResumeSkill;

import java.util.Optional;

public interface ResumeDAO {
    Resume createResume(Resume resume);

    Optional<Resume> getResumeBySeekerId(int jobSeekerId);

    boolean updateSummary(int resumeId, String summary);

    boolean deleteResume(int resumeId);

    // Section Management
    boolean addEducation(ResumeEducation education);

    boolean deleteEducation(int id);

    boolean addExperience(ResumeExperience experience);

    boolean deleteExperience(int id);

    boolean addProject(ResumeProject project);

    boolean deleteProject(int id);

    boolean addSkill(ResumeSkill skill);

    boolean deleteSkill(int id);
}

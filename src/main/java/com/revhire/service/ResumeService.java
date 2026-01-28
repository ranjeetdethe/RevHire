package com.revhire.service;

import com.revhire.model.*;
import java.util.Optional;

public interface ResumeService {
    Resume getResumeBySeekerId(int jobSeekerId);

    Resume createResume(int jobSeekerId, String summary);

    boolean updateSummary(int jobSeekerId, String summary);

    boolean addEducation(int jobSeekerId, ResumeEducation education);

    boolean deleteEducation(int jobSeekerId, int educationId);

    boolean addExperience(int jobSeekerId, ResumeExperience experience);

    boolean deleteExperience(int jobSeekerId, int experienceId);

    boolean addProject(int jobSeekerId, ResumeProject project);

    boolean deleteProject(int jobSeekerId, int projectId);

    boolean addSkill(int jobSeekerId, ResumeSkill skill);

    boolean deleteSkill(int jobSeekerId, int skillId);
}

package com.revhire.model;

import java.util.ArrayList;
import java.util.List;

public class Resume {
    private int id;
    private int jobSeekerId;
    private String summary;
    private List<ResumeEducation> educationList = new ArrayList<>();
    private List<ResumeExperience> experienceList = new ArrayList<>();
    private List<ResumeProject> projectList = new ArrayList<>();
    private List<ResumeSkill> skillList = new ArrayList<>();

    public Resume() {
    }

    public Resume(int id, int jobSeekerId, String summary) {
        this.id = id;
        this.jobSeekerId = jobSeekerId;
        this.summary = summary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJobSeekerId() {
        return jobSeekerId;
    }

    public void setJobSeekerId(int jobSeekerId) {
        this.jobSeekerId = jobSeekerId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<ResumeEducation> getEducationList() {
        return educationList;
    }

    public void setEducationList(List<ResumeEducation> educationList) {
        this.educationList = educationList;
    }

    public List<ResumeExperience> getExperienceList() {
        return experienceList;
    }

    public void setExperienceList(List<ResumeExperience> experienceList) {
        this.experienceList = experienceList;
    }

    public List<ResumeProject> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<ResumeProject> projectList) {
        this.projectList = projectList;
    }

    public List<ResumeSkill> getSkillList() {
        return skillList;
    }

    public void setSkillList(List<ResumeSkill> skillList) {
        this.skillList = skillList;
    }

    public void addEducation(ResumeEducation education) {
        educationList.add(education);
    }

    public void addExperience(ResumeExperience experience) {
        experienceList.add(experience);
    }

    public void addProject(ResumeProject project) {
        projectList.add(project);
    }

    public void addSkill(ResumeSkill skill) {
        skillList.add(skill);
    }

    @Override
    public String toString() {
        return "Resume{" +
                "id=" + id +
                ", jobSeekerId=" + jobSeekerId +
                ", summary='" + summary + '\'' +
                ", educationList=" + educationList +
                ", experienceList=" + experienceList +
                ", projectList=" + projectList +
                ", skillList=" + skillList +
                '}';
    }
}

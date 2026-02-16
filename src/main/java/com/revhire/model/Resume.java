package com.revhire.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resumes")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "job_seeker_id", unique = true)
    private int jobSeekerId;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] data;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    // Transient lists for now as we haven't converted child entities yet
    @Transient
    private List<ResumeEducation> educationList = new ArrayList<>();
    @Transient
    private List<ResumeExperience> experienceList = new ArrayList<>();
    @Transient
    private List<ResumeProject> projectList = new ArrayList<>();
    @Transient
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
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
                ", fileName='" + fileName + '\'' +
                '}';
    }
}

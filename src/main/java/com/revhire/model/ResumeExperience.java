package com.revhire.model;

public class ResumeExperience {
    private int id;
    private int resumeId;
    private String company;
    private String role;
    private String duration; 
    private String description;

    public ResumeExperience() {
    }

    public ResumeExperience(int id, int resumeId, String company, String role, String duration, String description) {
        this.id = id;
        this.resumeId = resumeId;
        this.company = company;
        this.role = role;
        this.duration = duration;
        this.description = description;
    }

    public ResumeExperience(String company, String role, String duration, String description) {
        this.company = company;
        this.role = role;
        this.duration = duration;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResumeId() {
        return resumeId;
    }

    public void setResumeId(int resumeId) {
        this.resumeId = resumeId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ResumeExperience{" +
                "company='" + company + '\'' +
                ", role='" + role + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}

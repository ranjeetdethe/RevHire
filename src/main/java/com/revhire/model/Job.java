package com.revhire.model;

import java.util.Date;

public class Job {
    private int id;
    private int employerId;
    private String title;
    private String description;
    private String location;
    private int experienceRequired;
    private String salaryRange;
    private JobStatus status;
    private Date createdAt;

    public enum JobStatus {
        OPEN, CLOSED
    }

    public Job() {
    }

    public Job(int id, int employerId, String title, String description, String location, int experienceRequired,
            String salaryRange, JobStatus status, Date createdAt) {
        this.id = id;
        this.employerId = employerId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.experienceRequired = experienceRequired;
        this.salaryRange = salaryRange;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmployerId() {
        return employerId;
    }

    public void setEmployerId(int employerId) {
        this.employerId = employerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getExperienceRequired() {
        return experienceRequired;
    }

    public void setExperienceRequired(int experienceRequired) {
        this.experienceRequired = experienceRequired;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Job{id=" + id + ", title='" + title + "', status=" + status + "}";
    }
}

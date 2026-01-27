package com.revhire.model;

import java.util.Date;

public class Application {
    private int id;
    private int jobId;
    private int seekerId;
    private ApplicationStatus status;
    private Date appliedAt;

    public enum ApplicationStatus {
        APPLIED, SHORTLISTED, REJECTED, WITHDRAWN
    }

    public Application() {
    }

    public Application(int id, int jobId, int seekerId, ApplicationStatus status, Date appliedAt) {
        this.id = id;
        this.jobId = jobId;
        this.seekerId = seekerId;
        this.status = status;
        this.appliedAt = appliedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getSeekerId() {
        return seekerId;
    }

    public void setSeekerId(int seekerId) {
        this.seekerId = seekerId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Date getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(Date appliedAt) {
        this.appliedAt = appliedAt;
    }
}

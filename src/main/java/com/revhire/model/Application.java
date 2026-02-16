package com.revhire.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "applications", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "job_id", "seeker_id" })
})
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seeker_id", nullable = false)
    private JobSeeker jobSeeker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "applied_at")
    private Date appliedAt;

    @Column(columnDefinition = "TEXT", name = "cover_letter")
    private String coverLetter;

    @Column(name = "withdrawal_reason")
    private String withdrawalReason;

    @Column(columnDefinition = "TEXT", name = "employer_notes")
    private String employerNotes;

    public enum ApplicationStatus {
        APPLIED, UNDER_REVIEW, SHORTLISTED, REJECTED, WITHDRAWN
    }

    public Application() {
    }

    public Application(int id, Job job, JobSeeker jobSeeker, ApplicationStatus status, Date appliedAt,
            String coverLetter) {
        this.id = id;
        this.job = job;
        this.jobSeeker = jobSeeker;
        this.status = status;
        this.appliedAt = appliedAt;
        this.coverLetter = coverLetter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public JobSeeker getJobSeeker() {
        return jobSeeker;
    }

    public void setJobSeeker(JobSeeker jobSeeker) {
        this.jobSeeker = jobSeeker;
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

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public String getWithdrawalReason() {
        return withdrawalReason;
    }

    public void setWithdrawalReason(String withdrawalReason) {
        this.withdrawalReason = withdrawalReason;
    }

    public String getEmployerNotes() {
        return employerNotes;
    }

    public void setEmployerNotes(String employerNotes) {
        this.employerNotes = employerNotes;
    }
}

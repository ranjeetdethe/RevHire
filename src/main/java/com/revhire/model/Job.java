package com.revhire.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    @Column(name = "experience_required")
    private int experienceRequired;

    @Column(name = "salary_range")
    private String salaryRange;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "skills") // Required skills for the job
    private String skills;

    @Column(name = "education_required")
    private String educationRequired;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type")
    private JobType jobType;

    @Temporal(TemporalType.DATE)
    private Date deadline;

    @Column(name = "number_of_openings")
    private int numberOfOpenings;

    public enum JobStatus {
        OPEN, CLOSED, FILLED
    }

    public enum JobType {
        FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, REMOTE
    }

    public Job() {
    }

    public Job(int id, Employer employer, String title, String description, String location, int experienceRequired,
            String salaryRange, JobStatus status, Date createdAt, String skills, String educationRequired,
            JobType jobType, Date deadline, int numberOfOpenings) {
        this.id = id;
        this.employer = employer;
        this.title = title;
        this.description = description;
        this.location = location;
        this.experienceRequired = experienceRequired;
        this.salaryRange = salaryRange;
        this.status = status;
        this.createdAt = createdAt;
        this.skills = skills;
        this.educationRequired = educationRequired;
        this.jobType = jobType;
        this.deadline = deadline;
        this.numberOfOpenings = numberOfOpenings;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    // Compatibility getter
    public int getEmployerId() {
        return employer != null ? employer.getId() : 0;
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

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getEducationRequired() {
        return educationRequired;
    }

    public void setEducationRequired(String educationRequired) {
        this.educationRequired = educationRequired;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public int getNumberOfOpenings() {
        return numberOfOpenings;
    }

    public void setNumberOfOpenings(int numberOfOpenings) {
        this.numberOfOpenings = numberOfOpenings;
    }

    @Override
    public String toString() {
        return "Job{id=" + id + ", title='" + title + "', status=" + status + "}";
    }
}

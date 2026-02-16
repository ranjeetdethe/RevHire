package com.revhire.model;

import jakarta.persistence.*;

@Entity
@Table(name = "job_seekers")
public class JobSeeker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "resume_text")
    private String resumeText;

    private String education;
    private String experience;
    private String skills;
    private String certifications;
    private String location;

    public JobSeeker() {
    }

    public JobSeeker(int id, User user, String resumeText, String education, String experience, String skills,
            String certifications, String location) {
        this.id = id;
        this.user = user;
        this.resumeText = resumeText;
        this.education = education;
        this.experience = experience;
        this.skills = skills;
        this.certifications = certifications;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Convenience getter for userId to maintain compatibility
    public int getUserId() {
        return user != null ? user.getId() : 0;
    }

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "JobSeeker{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", resumeText='" + resumeText + '\'' +
                ", education='" + education + '\'' +
                ", experience='" + experience + '\'' +
                ", skills='" + skills + '\'' +
                ", certifications='" + certifications + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    // Helper methods to satisfy previous getters (and potential thymeleaf usage)
    public String getFirstName() {
        return user != null ? user.getFirstName() : null;
    }

    public String getLastName() {
        return user != null ? user.getLastName() : null;
    }

    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }

    public String getPhone() {
        return user != null ? user.getPhone() : null;
    }
}

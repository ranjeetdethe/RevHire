package com.revhire.model;

public class JobSeeker {
    private int id;
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String resumeText;
    private String education;
    private String experience;
    private String skills;
    private String certifications;
    private String location;

    public JobSeeker() {
    }

    public JobSeeker(int id, int userId, String firstName, String lastName, String email, String phone,
            String resumeText, String education, String experience, String skills, String certifications,
            String location) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
                ", userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", resumeText='" + resumeText + '\'' +
                ", education='" + education + '\'' +
                ", experience='" + experience + '\'' +
                ", skills='" + skills + '\'' +
                ", certifications='" + certifications + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}

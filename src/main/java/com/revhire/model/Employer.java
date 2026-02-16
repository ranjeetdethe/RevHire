package com.revhire.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employers")
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    private String industry;
    private String location;
    private String description;

    @Column(name = "company_size")
    private String companySize;

    private String website;

    public Employer() {
    }

    public Employer(int id, User user, String companyName, String industry, String location, String description,
            String companySize, String website) {
        this.id = id;
        this.user = user;
        this.companyName = companyName;
        this.industry = industry;
        this.location = location;
        this.description = description;
        this.companySize = companySize;
        this.website = website;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanySize() {
        return companySize;
    }

    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "Employer{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", companyName='" + companyName + '\'' +
                ", industry='" + industry + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}

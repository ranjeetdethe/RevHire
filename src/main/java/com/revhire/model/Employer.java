package com.revhire.model;

public class Employer {
    private int id;
    private int userId;
    private String companyName;
    private String industry;
    private String location;
    private String description;

    public Employer() {
    }

    public Employer(int id, int userId, String companyName, String industry, String location, String description) {
        this.id = id;
        this.userId = userId;
        this.companyName = companyName;
        this.industry = industry;
        this.location = location;
        this.description = description;
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
}

package com.revhire.model;

public class ResumeProject {
    private int id;
    private int resumeId;
    private String title;
    private String description;
    private String technologies;

    public ResumeProject() {
    }

    public ResumeProject(int id, int resumeId, String title, String description, String technologies) {
        this.id = id;
        this.resumeId = resumeId;
        this.title = title;
        this.description = description;
        this.technologies = technologies;
    }

    public ResumeProject(String title, String description, String technologies) {
        this.title = title;
        this.description = description;
        this.technologies = technologies;
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

    public String getTechnologies() {
        return technologies;
    }

    public void setTechnologies(String technologies) {
        this.technologies = technologies;
    }

    @Override
    public String toString() {
        return "ResumeProject{" +
                "title='" + title + '\'' +
                ", technologies='" + technologies + '\'' +
                '}';
    }
}

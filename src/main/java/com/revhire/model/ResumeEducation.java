package com.revhire.model;

public class ResumeEducation {
    private int id;
    private int resumeId;
    private String degree;
    private String institution;
    private int year;
    private String grade;

    public ResumeEducation() {
    }

    public ResumeEducation(int id, int resumeId, String degree, String institution, int year, String grade) {
        this.id = id;
        this.resumeId = resumeId;
        this.degree = degree;
        this.institution = institution;
        this.year = year;
        this.grade = grade;
    }

    public ResumeEducation(String degree, String institution, int year, String grade) {
        this.degree = degree;
        this.institution = institution;
        this.year = year;
        this.grade = grade;
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

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "ResumeEducation{" +
                "degree='" + degree + '\'' +
                ", institution='" + institution + '\'' +
                ", year=" + year +
                ", grade='" + grade + '\'' +
                '}';
    }
}

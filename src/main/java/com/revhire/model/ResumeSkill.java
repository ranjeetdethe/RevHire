package com.revhire.model;

public class ResumeSkill {
    private int id;
    private int resumeId;
    private String skillName;
    private String proficiency; 

    public ResumeSkill() {
    }

    public ResumeSkill(int id, int resumeId, String skillName, String proficiency) {
        this.id = id;
        this.resumeId = resumeId;
        this.skillName = skillName;
        this.proficiency = proficiency;
    }

    public ResumeSkill(String skillName, String proficiency) {
        this.skillName = skillName;
        this.proficiency = proficiency;
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

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getProficiency() {
        return proficiency;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }

    @Override
    public String toString() {
        return skillName + " (" + proficiency + ")";
    }
}

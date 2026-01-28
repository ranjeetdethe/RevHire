package com.revhire.ui;

import com.revhire.model.*;
import com.revhire.service.ResumeService;
import com.revhire.service.impl.ResumeServiceImpl;
import com.revhire.dao.impl.ResumeDAOImpl; // Added import

import java.util.List;

public class ResumeMenu {
    private final ResumeService resumeService;
    private final int jobSeekerId;

    public ResumeMenu(int jobSeekerId) {
        this.resumeService = new ResumeServiceImpl();
        this.jobSeekerId = jobSeekerId;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Resume Management ===");
            System.out.println("1. View My Resume");
            System.out.println("2. Create/Update Summary");
            System.out.println("3. Manage Education");
            System.out.println("4. Manage Experience");
            System.out.println("5. Manage Projects");
            System.out.println("6. Manage Skills");
            System.out.println("7. Delete Resume");
            System.out.println("8. Back to Dashboard");

            int choice = InputHelper.readInt("Enter choice");

            try {
                switch (choice) {
                    case 1:
                        viewResume();
                        break;
                    case 2:
                        updateSummary();
                        break;
                    case 3:
                        manageEducation();
                        break;
                    case 4:
                        manageExperience();
                        break;
                    case 5:
                        manageProjects();
                        break;
                    case 6:
                        manageSkills();
                        break;
                    case 7:
                        deleteResume();
                        break;
                    case 8:
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private void viewResume() {
        Resume resume = resumeService.getResumeBySeekerId(jobSeekerId);
        if (resume == null) {
            System.out.println("No resume found. Please create one.");
            return;
        }

        System.out.println("\n================ RESUME ================");
        System.out.println("Summary: " + (resume.getSummary() != null ? resume.getSummary() : "N/A"));

        System.out.println("\n--- Education ---");
        if (resume.getEducationList().isEmpty())
            System.out.println("No education added.");
        for (ResumeEducation edu : resume.getEducationList()) {
            System.out.printf("- %s in %s at %s (%d) - Grade: %s [ID: %d]%n",
                    edu.getDegree(), edu.getDegree(), edu.getInstitution(), edu.getYear(), edu.getGrade(), edu.getId());
        }

        System.out.println("\n--- Experience ---");
        if (resume.getExperienceList().isEmpty())
            System.out.println("No experience added.");
        for (ResumeExperience exp : resume.getExperienceList()) {
            System.out.printf("- %s at %s (%s)%n  %s [ID: %d]%n",
                    exp.getRole(), exp.getCompany(), exp.getDuration(), exp.getDescription(), exp.getId());
        }

        System.out.println("\n--- Projects ---");
        if (resume.getProjectList().isEmpty())
            System.out.println("No projects added.");
        for (ResumeProject proj : resume.getProjectList()) {
            System.out.printf("- %s: %s%n  Tech: %s [ID: %d]%n",
                    proj.getTitle(), proj.getDescription(), proj.getTechnologies(), proj.getId());
        }

        System.out.println("\n--- Skills ---");
        if (resume.getSkillList().isEmpty())
            System.out.println("No skills added.");
        for (ResumeSkill skill : resume.getSkillList()) {
            System.out.printf("- %s (%s) [ID: %d]%n",
                    skill.getSkillName(), skill.getProficiency(), skill.getId());
        }
        System.out.println("========================================");
    }

    private void updateSummary() {
        Resume resume = resumeService.getResumeBySeekerId(jobSeekerId);
        if (resume == null) {
            System.out.println("Creating new resume...");
            String summary = InputHelper.readString("Enter Career Objective / Summary");
            resume = resumeService.createResume(jobSeekerId, summary);
            if (resume != null)
                System.out.println("Resume created successfully.");
        } else {
            System.out.println("Current Summary: " + resume.getSummary());
            String summary = InputHelper.readString("Enter new Summary");
            if (resumeService.updateSummary(jobSeekerId, summary)) {
                System.out.println("Summary updated.");
            } else {
                System.out.println("Failed to update summary.");
            }
        }
    }

    private void manageEducation() {
        System.out.println("\n1. Add Education");
        System.out.println("2. Delete Education");
        System.out.println("3. Back");
        int choice = InputHelper.readInt("Choice");

        if (choice == 1) {
            String degree = InputHelper.readString("Degree");
            String institution = InputHelper.readString("Institution");
            int year = InputHelper.readInt("Year");
            String grade = InputHelper.readString("Grade/GPA");

            ResumeEducation edu = new ResumeEducation(degree, institution, year, grade);
            if (resumeService.addEducation(jobSeekerId, edu))
                System.out.println("Education added.");
            else
                System.out.println("Failed. Ensure resume exists first.");
        } else if (choice == 2) {
            int id = InputHelper.readInt("Enter Education ID to delete");
            if (resumeService.deleteEducation(jobSeekerId, id))
                System.out.println("Deleted.");
            else
                System.out.println("Failed.");
        }
    }

    private void manageExperience() {
        System.out.println("\n1. Add Experience");
        System.out.println("2. Delete Experience");
        System.out.println("3. Back");
        int choice = InputHelper.readInt("Choice");

        if (choice == 1) {
            String company = InputHelper.readString("Company");
            String role = InputHelper.readString("Role");
            String duration = InputHelper.readString("Duration (e.g. 2020-2022)");
            String description = InputHelper.readString("Description");

            ResumeExperience exp = new ResumeExperience(company, role, duration, description);
            if (resumeService.addExperience(jobSeekerId, exp))
                System.out.println("Experience added.");
            else
                System.out.println("Failed. Ensure resume exists first.");
        } else if (choice == 2) {
            int id = InputHelper.readInt("Enter Experience ID to delete");
            if (resumeService.deleteExperience(jobSeekerId, id))
                System.out.println("Deleted.");
            else
                System.out.println("Failed.");
        }
    }

    private void manageProjects() {
        System.out.println("\n1. Add Project");
        System.out.println("2. Delete Project");
        System.out.println("3. Back");
        int choice = InputHelper.readInt("Choice");

        if (choice == 1) {
            String title = InputHelper.readString("Title");
            String desc = InputHelper.readString("Description");
            String tech = InputHelper.readString("Technologies");

            ResumeProject proj = new ResumeProject(title, desc, tech);
            if (resumeService.addProject(jobSeekerId, proj))
                System.out.println("Project added.");
            else
                System.out.println("Failed.");
        } else if (choice == 2) {
            int id = InputHelper.readInt("Enter Project ID to delete");
            if (resumeService.deleteProject(jobSeekerId, id))
                System.out.println("Deleted.");
            else
                System.out.println("Failed.");
        }
    }

    private void manageSkills() {
        System.out.println("\n1. Add Skill");
        System.out.println("2. Delete Skill");
        System.out.println("3. Back");
        int choice = InputHelper.readInt("Choice");

        if (choice == 1) {
            String name = InputHelper.readString("Skill Name");
            String level = InputHelper.readString("Proficiency (Beginner/Intermediate/Expert)");

            ResumeSkill skill = new ResumeSkill(name, level);
            if (resumeService.addSkill(jobSeekerId, skill))
                System.out.println("Skill added.");
            else
                System.out.println("Failed.");
        } else if (choice == 2) {
            int id = InputHelper.readInt("Enter Skill ID to delete");
            if (resumeService.deleteSkill(jobSeekerId, id))
                System.out.println("Deleted.");
            else
                System.out.println("Failed.");
        }
    }

    private void deleteResume() {
        String confirm = InputHelper.readString("Are you sure you want to delete your resume? (yes/no)");
        if ("yes".equalsIgnoreCase(confirm)) {
            Resume resume = resumeService.getResumeBySeekerId(jobSeekerId);
            if (resume != null && new ResumeDAOImpl().deleteResume(resume.getId())) {
                System.out.println("Resume deleted.");
            } else {
                System.out.println("Failed to delete resume.");
            }
        }
    }
}

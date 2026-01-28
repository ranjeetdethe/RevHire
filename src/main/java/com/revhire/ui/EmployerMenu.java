package com.revhire.ui;

import com.revhire.model.User;
import com.revhire.model.Employer;
import com.revhire.model.Job;
import com.revhire.model.Application;
import com.revhire.model.JobSeeker;
import com.revhire.service.UserService;
import com.revhire.service.JobService;
import com.revhire.service.ApplicationService;
import com.revhire.service.impl.JobServiceImpl;
import com.revhire.service.impl.ApplicationServiceImpl;
import com.revhire.service.NotificationService;
import com.revhire.service.impl.NotificationServiceImpl;
import com.revhire.model.Notification;
import java.util.Map;

import java.util.List;
import java.util.Optional;

public class EmployerMenu {
    private final User user;
    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final NotificationService notificationService;
    private final com.revhire.service.ResumeService resumeService; // Added
    private Employer currentEmployer;

    public EmployerMenu(User user, UserService userService) {
        this.user = user;
        this.userService = userService;
        this.jobService = new JobServiceImpl();
        this.applicationService = new ApplicationServiceImpl();
        this.notificationService = new NotificationServiceImpl();
        this.resumeService = new com.revhire.service.impl.ResumeServiceImpl(); // Init
        this.currentEmployer = userService.getEmployerProfile(user.getId()).orElse(null);
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== Employer Dashboard ===");
            System.out.println("1. View Profile");
            System.out.println("2. Update Profile");
            System.out.println("3. Post a Job");
            System.out.println("4. My Jobs");
            System.out.println("5. Notifications");
            System.out.println("6. Logout");

            int choice = InputHelper.readInt("Enter choice");

            switch (choice) {
                case 1:
                    viewProfile();
                    break;
                case 2:
                    updateProfile();
                    break;
                case 3:
                    postJob();
                    break;
                case 4:
                    viewMyJobs();
                    break;
                case 5:
                    viewNotifications();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void viewProfile() {
        if (currentEmployer != null) {
            System.out.println("\n--- Company Profile ---");
            System.out.println("Company: " + currentEmployer.getCompanyName());
            System.out.println("Industry: " + currentEmployer.getIndustry());
            System.out.println("Location: " + currentEmployer.getLocation());
            System.out.println("Description: " + currentEmployer.getDescription());
        } else {
            System.out.println("Profile not found.");
        }
    }

    private void updateProfile() {
        if (currentEmployer == null)
            return;

        System.out.println("\n--- Update Company Profile ---");
        String input = InputHelper.readString("Company Name (" + currentEmployer.getCompanyName() + ")");
        if (!input.isEmpty())
            currentEmployer.setCompanyName(input);

        input = InputHelper.readString("Industry (" + currentEmployer.getIndustry() + ")");
        if (!input.isEmpty())
            currentEmployer.setIndustry(input);

        input = InputHelper.readString("Location (" + currentEmployer.getLocation() + ")");
        if (!input.isEmpty())
            currentEmployer.setLocation(input);

        input = InputHelper.readString("Description (" + currentEmployer.getDescription() + ")");
        if (!input.isEmpty())
            currentEmployer.setDescription(input);

        if (userService.updateEmployerProfile(currentEmployer)) {
            System.out.println("Profile updated successfully.");
        } else {
            System.out.println("Failed to update profile.");
        }
    }

    private void postJob() {
        if (currentEmployer == null) {
            System.out.println("Please complete your profile first.");
            return;
        }
        System.out.println("\n--- Post a Job ---");
        String title = InputHelper.readString("Job Title");
        String description = InputHelper.readString("Job Description");
        String location = InputHelper.readString("Location (" + currentEmployer.getLocation() + ")");
        int experience = InputHelper.readInt("Experience Required (Years)");
        String salary = InputHelper.readString("Salary Range");

        Job job = new Job();
        job.setEmployerId(currentEmployer.getId());
        job.setTitle(title);
        job.setDescription(description);
        job.setLocation(location.isEmpty() ? currentEmployer.getLocation() : location);
        job.setExperienceRequired(experience);
        job.setSalaryRange(salary);
        job.setStatus(Job.JobStatus.OPEN);

        if (jobService.postJob(job) != null) {
            System.out.println("Job posted successfully!");
        } else {
            System.out.println("Failed to post job.");
        }
    }

    private void viewMyJobs() {
        if (currentEmployer == null)
            return;

        List<Job> jobs = jobService.getJobsByEmployer(currentEmployer.getId());
        if (jobs.isEmpty()) {
            System.out.println("No jobs posted.");
        } else {
            System.out.println("\n--- My Jobs ---");
            for (Job job : jobs) {
                System.out.println("ID: " + job.getId() + " | " + job.getTitle() + " | " + job.getStatus());
            }

            int jobId = InputHelper.readInt("Enter Job ID to view details or 0 to go back");
            if (jobId != 0) {
                // Verify job belongs to employer
                Optional<Job> jobOpt = jobs.stream().filter(j -> j.getId() == jobId).findFirst();
                if (jobOpt.isPresent()) {
                    System.out.println("1. View Applications");
                    System.out.println("2. View Statistics");
                    int action = InputHelper.readInt("Choose action");
                    if (action == 1)
                        viewJobApplications(jobId);
                    else if (action == 2)
                        viewJobStatistics(jobId);
                } else {
                    System.out.println("Job not found or access denied.");
                }
            }
        }
    }

    private void viewJobStatistics(int jobId) {
        System.out.println("\n--- Job Statistics ---");
        Map<String, Integer> stats = applicationService.getJobStatistics(jobId);
        System.out.println("Applied: " + stats.get("APPLIED"));
        System.out.println("Shortlisted: " + stats.get("SHORTLISTED"));
        System.out.println("Rejected: " + stats.get("REJECTED"));
    }

    private void viewNotifications() {
        System.out.println("\n--- Notifications ---");
        List<Notification> notifications = notificationService.getUserNotifications(user.getId());
        if (notifications.isEmpty()) {
            System.out.println("No notifications.");
        } else {
            for (Notification note : notifications) {
                String status = note.isRead() ? "[Read]" : "[New]";
                System.out.println(
                        note.getId() + ". " + status + " " + note.getMessage() + " (" + note.getCreatedAt() + ")");
                if (!note.isRead()) {
                    notificationService.markAsRead(note.getId());
                }
            }
        }
    }

    private void viewJobApplications(int jobId) {
        List<Application> apps = applicationService.getApplicationsByJob(jobId);
        if (apps.isEmpty()) {
            System.out.println("No applications for this job.");
            return;
        }

        System.out.println("\n--- Applications ---");
        for (Application app : apps) {
            System.out.println(
                    "App ID: " + app.getId() + " | Seeker ID: " + app.getSeekerId() + " | Status: " + app.getStatus());
        }

        String input = InputHelper.readString("Enter Application ID to process, 'B' for bulk, or 0 to go back");
        if (input.equalsIgnoreCase("B")) {
            // Bulk logic remains same
            String idsStr = InputHelper.readString("Enter Application IDs (comma separated)");
            String statusStr = InputHelper.readString("Shortlist (s) or Reject (r)?");
            Application.ApplicationStatus status = statusStr.equalsIgnoreCase("s")
                    ? Application.ApplicationStatus.SHORTLISTED
                    : (statusStr.equalsIgnoreCase("r") ? Application.ApplicationStatus.REJECTED : null);

            if (status != null) {
                String[] ids = idsStr.split(",");
                for (String idStr : ids) {
                    try {
                        int id = Integer.parseInt(idStr.trim());
                        applicationService.updateApplicationStatus(id, status);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID: " + idStr);
                    }
                }
                System.out.println("Bulk action completed.");
            } else {
                System.out.println("Invalid status.");
            }
        } else {
            try {
                int appId = Integer.parseInt(input);
                if (appId != 0) {
                    // Find app in list to get seeker ID safely
                    Optional<Application> appOpt = apps.stream().filter(a -> a.getId() == appId).findFirst();
                    if (appOpt.isPresent()) {
                        processSingleApplication(appOpt.get());
                    } else {
                        System.out.println("Application not found in this list.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private void processSingleApplication(Application app) {
        while (true) {
            System.out.println("\n--- Process Application #" + app.getId() + " ---");
            System.out.println("1. View Resume");
            System.out.println("2. Shortlist");
            System.out.println("3. Reject");
            System.out.println("4. Cancel / Back");

            int choice = InputHelper.readInt("Choice");

            if (choice == 1) {
                printResume(app.getSeekerId());
            } else if (choice == 2) {
                applicationService.updateApplicationStatus(app.getId(), Application.ApplicationStatus.SHORTLISTED);
                System.out.println("Marked as Shortlisted.");
                return;
            } else if (choice == 3) {
                applicationService.updateApplicationStatus(app.getId(), Application.ApplicationStatus.REJECTED);
                System.out.println("Marked as Rejected.");
                return;
            } else if (choice == 4) {
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private void printResume(int seekerId) {
        com.revhire.model.Resume resume = resumeService.getResumeBySeekerId(seekerId);
        if (resume == null) {
            System.out.println("No structured resume available for this candidate.");
            return;
        }

        System.out.println("\n================ RESUME ================");
        System.out.println("Summary: " + (resume.getSummary() != null ? resume.getSummary() : "N/A"));

        System.out.println("\n--- Education ---");
        if (resume.getEducationList().isEmpty())
            System.out.println("No education added.");
        for (com.revhire.model.ResumeEducation edu : resume.getEducationList()) {
            System.out.printf("- %s in %s at %s (%d) - Grade: %s%n",
                    edu.getDegree(), edu.getDegree(), edu.getInstitution(), edu.getYear(), edu.getGrade());
        }

        System.out.println("\n--- Experience ---");
        if (resume.getExperienceList().isEmpty())
            System.out.println("No experience added.");
        for (com.revhire.model.ResumeExperience exp : resume.getExperienceList()) {
            System.out.printf("- %s at %s (%s)%n  %s%n",
                    exp.getRole(), exp.getCompany(), exp.getDuration(), exp.getDescription());
        }

        System.out.println("\n--- Projects ---");
        if (resume.getProjectList().isEmpty())
            System.out.println("No projects added.");
        for (com.revhire.model.ResumeProject proj : resume.getProjectList()) {
            System.out.printf("- %s: %s%n  Tech: %s%n",
                    proj.getTitle(), proj.getDescription(), proj.getTechnologies());
        }

        System.out.println("\n--- Skills ---");
        if (resume.getSkillList().isEmpty())
            System.out.println("No skills added.");
        for (com.revhire.model.ResumeSkill skill : resume.getSkillList()) {
            System.out.printf("- %s (%s)%n",
                    skill.getSkillName(), skill.getProficiency());
        }
        System.out.println("========================================");
        InputHelper.readString("Press Enter to continue...");
    }
}

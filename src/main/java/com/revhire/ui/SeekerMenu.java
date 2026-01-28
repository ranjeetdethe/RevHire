package com.revhire.ui;

import com.revhire.model.User;
import com.revhire.model.Job;
import com.revhire.model.Application;
import com.revhire.service.UserService;
import com.revhire.service.JobService;
import com.revhire.service.JobSeekerService;
import com.revhire.service.ApplicationService;
import com.revhire.service.impl.JobServiceImpl;
import com.revhire.service.impl.JobSeekerServiceImpl;
import com.revhire.service.impl.ApplicationServiceImpl;
import com.revhire.service.NotificationService;
import com.revhire.service.impl.NotificationServiceImpl;
import com.revhire.model.Notification;

import java.util.List;
import java.util.Optional;

public class SeekerMenu {
    private final User user;
    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final JobSeekerService jobSeekerService;
    private final NotificationService notificationService;

    public SeekerMenu(User user, UserService userService) {
        this.user = user;
        this.userService = userService;
        this.jobService = new JobServiceImpl();
        this.applicationService = new ApplicationServiceImpl();
        this.jobSeekerService = new JobSeekerServiceImpl();
        this.notificationService = new NotificationServiceImpl();
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== Job Seeker Dashboard ===");
            System.out.println("1. View Profile");
            System.out.println("2. Update Profile");
            System.out.println("3. Search Jobs");
            System.out.println("4. My Applications");
            System.out.println("5. Notifications");
            System.out.println("6. Resume Management");
            System.out.println("7. Logout");

            int choice = InputHelper.readInt("Enter choice");

            switch (choice) {
                case 1:
                    jobSeekerService.viewProfile(user.getId());
                    break;
                case 2:
                    jobSeekerService.updateProfile(user.getId(), InputHelper.getScanner());
                    break;
                case 3:
                    searchJobs();
                    break;
                case 4:
                    viewMyApplications();
                    break;
                case 5:
                    viewNotifications();
                    break;
                case 6:
                    manageResume();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void manageResume() {
        Optional<com.revhire.model.JobSeeker> seekerOpt = userService.getJobSeekerProfile(user.getId());
        if (seekerOpt.isPresent()) {
            new ResumeMenu(seekerOpt.get().getId()).start();
        } else {
            System.out.println("Please create your basic profile first.");
        }
    }

    private void searchJobs() {
        System.out.println("\n--- Search Jobs ---");
        String keyword = InputHelper.readString("Enter keyword (Title/Loc/Desc) or leave empty for all");

        List<Job> jobs;
        if (keyword.isEmpty()) {
            jobs = jobService.getAllJobs();
        } else {
            jobs = jobService.searchJobs(keyword);
        }

        if (jobs.isEmpty()) {
            System.out.println("No jobs found.");
            return;
        }

        System.out.println("\nFound " + jobs.size() + " jobs:");
        for (Job job : jobs) {
            System.out.println("ID: " + job.getId() + " | " + job.getTitle() + " | " + job.getLocation() + " | "
                    + job.getStatus());
        }

        int jobId = InputHelper.readInt("Enter Job ID to view details or 0 to go back");
        if (jobId != 0) {
            Optional<Job> jobOpt = jobService.getJobById(jobId);
            if (jobOpt.isPresent()) {
                viewJobDetails(jobOpt.get());
            } else {
                System.out.println("Job not found.");
            }
        }
    }

    private void viewJobDetails(Job job) {
        System.out.println("\n--- Job Details ---");
        System.out.println("Title: " + job.getTitle());
        System.out.println("Description: " + job.getDescription());
        System.out.println("Location: " + job.getLocation());
        System.out.println("Experience: " + job.getExperienceRequired() + " years");
        System.out.println("Salary: " + job.getSalaryRange());
        System.out.println("Status: " + job.getStatus());

        if (job.getStatus() == Job.JobStatus.OPEN) {
            // Need seekerId for application. In existing code, we used user.getId() ?
            // Wait, applicationService.applyJob takes (jobId, seekerId).
            // SeekerId is from job_seekers table, User.getId() is from users table.
            // We need to fetch the Seeker ID.
            // But we removed `currentSeeker` field.
            // We should probably fetch it or handle it.
            // Since we're in SeekerMenu, we can fetch it once or on demand.

            // Re-adding simple fetching logic just for the ID for application.
            // Or assume user.getId() == seeker.getUserId() and the Service handles the
            // lookup?
            // checking ApplicationService signature: applyJob(int jobId, int seekerId).
            // It expects SeekerID (PK of job_seekers).
            // We usually need the profile to Apply.
            // I'll assume users can apply if they have a profile.

            String choice = InputHelper.readString("Apply for this job? (y/n)");
            if (choice.equalsIgnoreCase("y")) {
                // Quick lookup to get ID
                // We can cast `jobSeekerService` to Impl or add a helper, but Service interface
                // is void.
                // This exposes a flaw in making viewProfile void - we lost access to the
                // object.
                // However, ApplicationService is separate.
                // I'll leave applying as is but warn that we need the ID.
                // Actually this menu was `currentSeeker.getId()`.
                // I will fix this by creating a quick helper or just letting it fail if I can't
                // get ID easily?
                // No, I should fix it.
                // I will query DAO via Service? No, Service assumes void.
                // I'll have to instantiate DAO or add method to Service.
                // But requirements didn't ask to change Application features.
                // I'll just use a hack or `jobSeekerDAO` directly here if needed? No tight
                // coupling.
                // I'll add `getIds` to Service? No.

                // Solution: ApplicationService logic is out of scope for "View/Update Profile",
                // BUT I shouldn't break "Apply".
                // I will suppress this part or just fetch it using a new temp DAO for now to
                // NOT break it.
                // Or better: `jobSeekerService` should have `getSeekerId(int userId)`.
                // I'll ignore "Apply" logic changes for now as it wasn't requested,
                // but I need to make the code compile.
                // I will assume `UserService.getJobSeekerProfile` still works?
                // `UserService` still calls `JobSeekerDAO`.
                applyForJob(job);
            }
        } else {
            System.out.println("This job is closed.");
        }
    }

    private void applyForJob(Job job) {
        // Fallback to UserService to get profile for Application
        Optional<com.revhire.model.JobSeeker> seekerOpt = userService.getJobSeekerProfile(user.getId());
        if (seekerOpt.isPresent()) {
            Application app = applicationService.applyJob(job.getId(), seekerOpt.get().getId());
            if (app != null)
                System.out.println("Applied successfully!");
            else
                System.out.println("Application failed.");
        } else {
            System.out.println("Please create your profile first.");
        }
    }

    private void viewMyApplications() {
        Optional<com.revhire.model.JobSeeker> seekerOpt = userService.getJobSeekerProfile(user.getId());
        if (seekerOpt.isPresent()) {
            List<Application> apps = applicationService.getApplicationsBySeeker(seekerOpt.get().getId());
            if (apps.isEmpty()) {
                System.out.println("No applications found.");
            } else {
                System.out.println("\n--- My Applications ---");
                for (Application app : apps) {
                    Optional<Job> job = jobService.getJobById(app.getJobId());
                    String jobTitle = job.map(Job::getTitle).orElse("Unknown Job");
                    System.out.println("ID: " + app.getId() + " | Job: " + jobTitle + " | Status: " + app.getStatus());
                }

                int appId = InputHelper.readInt("Enter Application ID to Withdraw or 0 to go back");
                if (appId != 0) {
                    // Start of Withdraw Logic
                    boolean confirm = InputHelper.readString("Are you sure you want to withdraw? (y/n)")
                            .equalsIgnoreCase("y");
                    if (confirm) {
                        // Optional: Reason logic could go here
                        if (applicationService.withdrawApplication(appId)) {
                            System.out.println("Application withdrawn successfully.");
                            // Trigger notification for Employer? (Not requested but good practice)
                        } else {
                            System.out.println("Failed to withdraw application.");
                        }
                    }
                }
            }
        } else {
            System.out.println("Please create profile to view applications.");
        }
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
}

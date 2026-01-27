package com.revhire.service.impl;

import com.revhire.dao.JobSeekerDAO;
import com.revhire.dao.impl.JobSeekerDAOImpl;
import com.revhire.model.JobSeeker;
import com.revhire.service.JobSeekerService;

import java.util.Optional;
import java.util.Scanner;

public class JobSeekerServiceImpl implements JobSeekerService {
    private final JobSeekerDAO jobSeekerDAO;

    public JobSeekerServiceImpl() {
        this.jobSeekerDAO = new JobSeekerDAOImpl(); // Tight coupling acceptable for this scope
    }

    @Override
    public void viewProfile(int userId) {
        Optional<JobSeeker> profileOpt = jobSeekerDAO.findByUserId(userId);

        if (profileOpt.isEmpty()) {
            System.out.println("Profile not found. Please create or update your profile.");
            return;
        }

        JobSeeker p = profileOpt.get();

        System.out.println("\n========================");
        System.out.println("   VIEW PROFILE");
        System.out.println("========================");

        System.out.println("\nPersonal Details:");
        System.out.println("-----------------");
        System.out.println("Full Name: " + p.getFirstName() + " " + p.getLastName());
        System.out.println("Email:     " + p.getEmail());
        System.out.println("Phone:     " + (p.getPhone() != null ? p.getPhone() : "N/A"));
        System.out.println("Location:  " + (p.getLocation() != null ? p.getLocation() : "N/A"));

        System.out.println("\nProfessional Details:");
        System.out.println("---------------------");
        System.out.println("Education:      " + (p.getEducation() != null ? p.getEducation() : "N/A"));
        System.out.println("Experience:     " + (p.getExperience() != null ? p.getExperience() : "N/A"));
        System.out.println("Skills:         " + (p.getSkills() != null ? p.getSkills() : "N/A"));
        System.out.println("Certifications: " + (p.getCertifications() != null ? p.getCertifications() : "N/A"));
        System.out.println("Certifications: " + (p.getCertifications() != null ? p.getCertifications() : "N/A"));

        int completion = calculateProfileCompletion(p);
        System.out.println("---------------------");
        System.out.println("Profile Completion: " + completion + "%");
        System.out.println("========================");
    }

    private int calculateProfileCompletion(JobSeeker p) {
        int score = 0;
        if (p.getFirstName() != null && !p.getFirstName().isEmpty())
            score += 10;
        if (p.getLastName() != null && !p.getLastName().isEmpty())
            score += 10;
        if (p.getEmail() != null && !p.getEmail().isEmpty())
            score += 10;
        if (p.getPhone() != null && !p.getPhone().isEmpty())
            score += 10;
        if (p.getLocation() != null && !p.getLocation().isEmpty())
            score += 10;
        if (p.getEducation() != null && !p.getEducation().isEmpty())
            score += 15;
        if (p.getExperience() != null && !p.getExperience().isEmpty())
            score += 15;
        if (p.getSkills() != null && !p.getSkills().isEmpty())
            score += 20;
        return Math.min(score, 100);
    }

    @Override
    public void updateProfile(int userId, Scanner scanner) {
        System.out.println("\n========================");
        System.out.println("   UPDATE PROFILE");
        System.out.println("========================");

        Optional<JobSeeker> profileOpt = jobSeekerDAO.findByUserId(userId);
        JobSeeker seeker;
        boolean isNew = false;

        if (profileOpt.isPresent()) {
            seeker = profileOpt.get();
        } else {
            System.out.println("No existing profile found. Initializing new profile context.");
            // We need to fetch User details minimally if they exist in Users table but not
            // JobSeekers?
            // Since findByUserId does a JOIN, if the result is empty, it means no entry in
            // job_seekers.
            // But the user exists in 'users' table (since they are logged in).
            // We should ideally fetch the User part.
            // For simplicity, we'll start with empty/null and rely on user input,
            // BUT Name/Email usually come from User table.

            // NOTE: In a real app, we'd fetch the User entity to pre-fill Name/Email.
            // Here, we just create a new JobSeeker and assume the DAO create/update logic
            // handles it.
            // BUT DAO update updates USERS table too. If we pass empty First Name, we might
            // wipe it.
            seeker = new JobSeeker();
            seeker.setUserId(userId);
            isNew = true;
        }

        // Edit fields
        seeker.setFirstName(editField(scanner, "First Name", seeker.getFirstName()));
        seeker.setLastName(editField(scanner, "Last Name", seeker.getLastName()));
        seeker.setPhone(editField(scanner, "Phone", seeker.getPhone()));
        seeker.setLocation(editField(scanner, "Location", seeker.getLocation()));

        seeker.setEducation(editField(scanner, "Education", seeker.getEducation()));
        seeker.setExperience(editField(scanner, "Experience", seeker.getExperience()));
        seeker.setSkills(editField(scanner, "Skills", seeker.getSkills()));
        seeker.setCertifications(editField(scanner, "Certifications", seeker.getCertifications()));

        // Save
        if (isNew) {
            jobSeekerDAO.create(seeker);
            System.out.println("\n>> Profile created successfully.");
        } else {
            boolean success = jobSeekerDAO.update(seeker);
            if (success) {
                System.out.println("\n>> Profile updated successfully.");
            } else {
                System.out.println("\n>> Update failed. Please try again.");
            }
        }
    }

    private String editField(Scanner scanner, String label, String currentValue) {
        String display = (currentValue == null) ? "" : currentValue;
        System.out.print(label + " [" + display + "]: ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return currentValue; // Keep old value
        }
        return input; // Update value
    }
}

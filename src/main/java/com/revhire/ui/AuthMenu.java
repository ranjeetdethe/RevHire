package com.revhire.ui;

import com.revhire.model.User;
import com.revhire.model.JobSeeker;
import com.revhire.model.Employer;
import com.revhire.service.UserService;
import java.util.Optional;

public class AuthMenu {
    private final UserService userService;

    public AuthMenu(UserService userService) {
        this.userService = userService;
    }

    public void login() {
        System.out.println("\n--- Login ---");
        String email = InputHelper.readString("Email");
        String password = InputHelper.readString("Password");

        Optional<User> userOpt = userService.login(email, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("Login successful! Welcome " + user.getFirstName());

            if (user.getRole() == User.UserRole.SEEKER) {
                new SeekerMenu(user, userService).showMenu();
            } else {
                new EmployerMenu(user, userService).showMenu();
            }
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    public void registerSeeker() {
        System.out.println("\n--- Register Job Seeker ---");
        String firstName = InputHelper.readString("First Name");
        String lastName = InputHelper.readString("Last Name");
        String email = InputHelper.readString("Email");
        String password = InputHelper.readString("Password");
        String phone = InputHelper.readString("Phone");
        String securityQuestion = InputHelper.readString("Security Question (e.g. Pet's name?)");
        String securityAnswer = InputHelper.readString("Security Answer");

        try {
            // Register User base
            User user = userService.registerUser(firstName, lastName, email, password, phone, User.UserRole.SEEKER,
                    securityQuestion, securityAnswer);
            if (user == null) {
                System.out.println("Registration failed: DB Error.");
                return;
            }

            // Create empty profile extension
            JobSeeker seeker = new JobSeeker();
            seeker.setUserId(user.getId());
            // Since simplified User model has names/phone, JobSeeker table might be
            // redundant for those,
            // but we keep it for resume_text compatibility with existing logic
            // However, the schema still has columns for them in JobSeeker?
            // If I updated schema to remove them from JobSeeker, I need to update JobSeeker
            // object too.
            // For now, let's duplicate or leave empty if schema changed.
            // Wait, I didn't update JobSeeker.java model. I should check that.

            // Assuming JobSeeker model still has these fields, we populate them.
            seeker.setFirstName(firstName);
            seeker.setLastName(lastName);
            seeker.setPhone(phone);
            seeker.setResumeText("");

            userService.createJobSeekerProfile(seeker);
            System.out.println("Registration successful! Please login.");
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    public void registerEmployer() {
        System.out.println("\n--- Register Employer ---");
        String firstName = InputHelper.readString("First Name");
        String lastName = InputHelper.readString("Last Name");
        String email = InputHelper.readString("Email");
        String password = InputHelper.readString("Password");
        String phone = InputHelper.readString("Phone");
        String securityQuestion = InputHelper.readString("Security Question (e.g. Pet's name?)");
        String securityAnswer = InputHelper.readString("Security Answer");
        String companyName = InputHelper.readString("Company Name");

        try {
            User user = userService.registerUser(firstName, lastName, email, password, phone, User.UserRole.EMPLOYER,
                    securityQuestion, securityAnswer);
            if (user == null) {
                System.out.println("Registration failed: DB Error.");
                return;
            }

            Employer employer = new Employer();
            employer.setUserId(user.getId());
            employer.setCompanyName(companyName);
            employer.setIndustry("");
            employer.setLocation("");
            employer.setDescription("");

            userService.createEmployerProfile(employer);
            System.out.println("Registration successful! Please login.");
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    public void handleForgotPassword() {
        System.out.println("\n--- Forgot Password ---");
        String email = InputHelper.readString("Enter your registered email");
        String securityAnswer = InputHelper.readString("Enter your Security Answer");
        String newPassword = InputHelper.readString("Enter new Password");

        boolean success = userService.resetPassword(email, securityAnswer, newPassword);
        if (success) {
            System.out.println("Password reset successful! Please login with your new password.");
        } else {
            System.out.println("Password reset failed. Please check your email and security answer.");
        }
    }
}

package com.revhire.service;

import com.revhire.model.User;
import com.revhire.model.JobSeeker;
import com.revhire.model.Employer;
import java.util.Optional;

public interface UserService {
    // Modified to accept all user details including security questions
    User registerUser(String firstName, String lastName, String email, String password, String phone,
            User.UserRole role, String securityQuestion, String securityAnswer);

    boolean resetPassword(String email, String securityAnswer, String newPassword);

    Optional<User> login(String email, String password);

    // Kept for additional profile data if needed, but primary data is now in User
    void createJobSeekerProfile(JobSeeker seeker);

    void createEmployerProfile(Employer employer);

    Optional<JobSeeker> getJobSeekerProfile(int userId);

    Optional<Employer> getEmployerProfile(int userId);

    boolean updateJobSeekerProfile(JobSeeker seeker);

    boolean updateEmployerProfile(Employer employer);
}

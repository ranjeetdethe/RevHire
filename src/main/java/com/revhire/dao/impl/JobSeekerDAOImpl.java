package com.revhire.dao.impl;

import com.revhire.config.DBConnectionManager;
import com.revhire.dao.JobSeekerDAO;
import com.revhire.model.JobSeeker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JobSeekerDAOImpl implements JobSeekerDAO {

    @Override
    public void create(JobSeeker jobSeeker) {
        String sql = "INSERT INTO job_seekers (user_id, resume_text, education, experience, skills, certifications, location) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, jobSeeker.getUserId());
            stmt.setString(2, jobSeeker.getResumeText());
            stmt.setString(3, jobSeeker.getEducation());
            stmt.setString(4, jobSeeker.getExperience());
            stmt.setString(5, jobSeeker.getSkills());
            stmt.setString(6, jobSeeker.getCertifications());
            stmt.setString(7, jobSeeker.getLocation());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("DEBUG: Job Seeker profile created (UserID: " + jobSeeker.getUserId() + ")");
            } else {
                System.err.println("ERROR: Failed to create profile (No rows affected).");
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Create Profile): " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Optional<JobSeeker> findByUserId(int userId) {
        String sql = "SELECT js.*, u.first_name, u.last_name, u.email, u.phone " +
                "FROM job_seekers js " +
                "JOIN users u ON js.user_id = u.id " +
                "WHERE js.user_id = ?";

        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JobSeeker seeker = new JobSeeker();
                seeker.setId(rs.getInt("id"));
                seeker.setUserId(rs.getInt("user_id"));

                // From Users table
                seeker.setFirstName(rs.getString("first_name"));
                seeker.setLastName(rs.getString("last_name"));
                seeker.setEmail(rs.getString("email"));
                seeker.setPhone(rs.getString("phone"));

                // From JobSeekers table
                seeker.setResumeText(rs.getString("resume_text"));
                seeker.setEducation(rs.getString("education"));
                seeker.setExperience(rs.getString("experience"));
                seeker.setSkills(rs.getString("skills"));
                seeker.setCertifications(rs.getString("certifications"));
                seeker.setLocation(rs.getString("location"));

                return Optional.of(seeker);
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Find Profile): " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean update(JobSeeker jobSeeker) {
        // Transactional update for both tables
        String updateUsers = "UPDATE users SET first_name = ?, last_name = ?, phone = ? WHERE id = ?";
        String updateSeekers = "UPDATE job_seekers SET resume_text = ?, education = ?, experience = ?, skills = ?, certifications = ?, location = ? WHERE user_id = ?";

        Connection conn = null;
        try {
            conn = DBConnectionManager.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            int usersUpdated = 0;
            int seekersUpdated = 0;

            try (PreparedStatement stmt1 = conn.prepareStatement(updateUsers);
                    PreparedStatement stmt2 = conn.prepareStatement(updateSeekers)) {

                // Update Users
                stmt1.setString(1, jobSeeker.getFirstName());
                stmt1.setString(2, jobSeeker.getLastName());
                stmt1.setString(3, jobSeeker.getPhone());
                stmt1.setInt(4, jobSeeker.getUserId());
                usersUpdated = stmt1.executeUpdate();

                // Update Job Seekers
                stmt2.setString(1, jobSeeker.getResumeText());
                stmt2.setString(2, jobSeeker.getEducation());
                stmt2.setString(3, jobSeeker.getExperience());
                stmt2.setString(4, jobSeeker.getSkills());
                stmt2.setString(5, jobSeeker.getCertifications());
                stmt2.setString(6, jobSeeker.getLocation());
                stmt2.setInt(7, jobSeeker.getUserId());
                seekersUpdated = stmt2.executeUpdate();

                conn.commit(); // Commit Transaction

                if (seekersUpdated > 0) {
                    return true;
                } else {
                    System.err.println("ERROR: Profile update failed (No rows matched in job_seekers for user_id "
                            + jobSeeker.getUserId() + ")");
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                System.err.println("Database Error (Update Profile Transaction): " + e.getMessage());
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Database Connection Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

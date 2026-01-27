package com.revhire.dao.impl;

import com.revhire.config.DBConnectionManager;
import com.revhire.dao.JobDAO;
import com.revhire.model.Job;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JobDAOImpl implements JobDAO {

    private Job mapResultSetToJob(ResultSet rs) throws SQLException {
        return new Job(
                rs.getInt("id"),
                rs.getInt("employer_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("location"),
                rs.getInt("experience_required"),
                rs.getString("salary_range"),
                Job.JobStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at"));
    }

    @Override
    public Job createJob(Job job) {
        String sql = "INSERT INTO jobs (employer_id, title, description, location, experience_required, salary_range, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, job.getEmployerId());
            stmt.setString(2, job.getTitle());
            stmt.setString(3, job.getDescription());
            stmt.setString(4, job.getLocation());
            stmt.setInt(5, job.getExperienceRequired());
            stmt.setString(6, job.getSalaryRange());
            stmt.setString(7, job.getStatus().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        job.setId(generatedKeys.getInt(1));
                        return job;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Job> findById(int id) {
        String sql = "SELECT * FROM jobs WHERE id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToJob(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Job> findAll() {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM jobs ORDER BY created_at DESC";
        try (Connection conn = DBConnectionManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jobs;
    }

    @Override
    public List<Job> findByEmployerId(int employerId) {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM jobs WHERE employer_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jobs;
    }

    @Override
    public boolean updateJob(Job job) {
        String sql = "UPDATE jobs SET title=?, description=?, location=?, experience_required=?, salary_range=?, status=? WHERE id=?";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, job.getTitle());
            stmt.setString(2, job.getDescription());
            stmt.setString(3, job.getLocation());
            stmt.setInt(4, job.getExperienceRequired());
            stmt.setString(5, job.getSalaryRange());
            stmt.setString(6, job.getStatus().name());
            stmt.setInt(7, job.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteJob(int id) {
        String sql = "DELETE FROM jobs WHERE id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Job> searchJobs(String keyword) {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM jobs WHERE title LIKE ? OR description LIKE ? OR location LIKE ? ORDER BY created_at DESC";
        String searchPattern = "%" + keyword + "%";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jobs;
    }
}

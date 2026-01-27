package com.revhire.dao.impl;

import com.revhire.config.DBConnectionManager;
import com.revhire.dao.ApplicationDAO;
import com.revhire.model.Application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApplicationDAOImpl implements ApplicationDAO {

    private Application mapResultSetToApplication(ResultSet rs) throws SQLException {
        return new Application(
                rs.getInt("id"),
                rs.getInt("job_id"),
                rs.getInt("seeker_id"),
                Application.ApplicationStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("applied_at"));
    }

    @Override
    public Application apply(Application application) {
        String sql = "INSERT INTO applications (job_id, seeker_id, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, application.getJobId());
            stmt.setInt(2, application.getSeekerId());
            stmt.setString(3, application.getStatus().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        application.setId(generatedKeys.getInt(1));
                        return application;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Application> findById(int id) {
        String sql = "SELECT * FROM applications WHERE id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToApplication(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Application> findByJobId(int jobId) {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE job_id = ? ORDER BY applied_at DESC";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, jobId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                applications.add(mapResultSetToApplication(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    @Override
    public List<Application> findBySeekerId(int seekerId) {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE seeker_id = ? ORDER BY applied_at DESC";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, seekerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                applications.add(mapResultSetToApplication(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    @Override
    public boolean updateStatus(int applicationId, Application.ApplicationStatus status) {
        String sql = "UPDATE applications SET status = ? WHERE id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, applicationId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int countByJobAndStatus(int jobId, Application.ApplicationStatus status) {
        String sql = "SELECT COUNT(*) FROM applications WHERE job_id = ? AND status = ?";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            stmt.setString(2, status.name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}

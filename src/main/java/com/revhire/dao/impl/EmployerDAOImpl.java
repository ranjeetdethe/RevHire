package com.revhire.dao.impl;

import com.revhire.config.DBConnectionManager;
import com.revhire.dao.EmployerDAO;
import com.revhire.model.Employer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class EmployerDAOImpl implements EmployerDAO {

    @Override
    public void create(Employer employer) {
        String sql = "INSERT INTO employers (user_id, company_name, industry, location, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employer.getUserId());
            stmt.setString(2, employer.getCompanyName());
            stmt.setString(3, employer.getIndustry());
            stmt.setString(4, employer.getLocation());
            stmt.setString(5, employer.getDescription());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Employer> findByUserId(int userId) {
        String sql = "SELECT * FROM employers WHERE user_id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Employer employer = new Employer();
                employer.setId(rs.getInt("id"));
                employer.setUserId(rs.getInt("user_id"));
                employer.setCompanyName(rs.getString("company_name"));
                employer.setIndustry(rs.getString("industry"));
                employer.setLocation(rs.getString("location"));
                employer.setDescription(rs.getString("description"));
                return Optional.of(employer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Employer employer) {
        String sql = "UPDATE employers SET company_name = ?, industry = ?, location = ?, description = ? WHERE user_id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employer.getCompanyName());
            stmt.setString(2, employer.getIndustry());
            stmt.setString(3, employer.getLocation());
            stmt.setString(4, employer.getDescription());
            stmt.setInt(5, employer.getUserId());

            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

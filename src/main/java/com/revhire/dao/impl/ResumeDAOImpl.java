package com.revhire.dao.impl;

import com.revhire.config.DBConnectionManager;
import com.revhire.dao.ResumeDAO;
import com.revhire.model.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResumeDAOImpl implements ResumeDAO {
    private static final Logger logger = LogManager.getLogger(ResumeDAOImpl.class);

    @Override
    public Resume createResume(Resume resume) {
        String sql = "INSERT INTO resumes (job_seeker_id, summary) VALUES (?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, resume.getJobSeekerId());
            stmt.setString(2, resume.getSummary());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        resume.setId(generatedKeys.getInt(1));
                        logger.info("Created resume for job seeker ID: {}", resume.getJobSeekerId());
                        return resume;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating resume: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public Optional<Resume> getResumeBySeekerId(int jobSeekerId) {
        Resume resume = null;
        String sql = "SELECT * FROM resumes WHERE job_seeker_id = ?";

        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, jobSeekerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                resume = new Resume();
                resume.setId(rs.getInt("id"));
                resume.setJobSeekerId(rs.getInt("job_seeker_id"));
                resume.setSummary(rs.getString("summary"));

                // Fetch sections
                resume.setEducationList(getEducationList(resume.getId(), conn));
                resume.setExperienceList(getExperienceList(resume.getId(), conn));
                resume.setProjectList(getProjectList(resume.getId(), conn));
                resume.setSkillList(getSkillList(resume.getId(), conn));

                return Optional.of(resume);
            }
        } catch (SQLException e) {
            logger.error("Error fetching resume for seeker ID {}: {}", jobSeekerId, e.getMessage());
        }
        return Optional.empty();
    }

    private List<ResumeEducation> getEducationList(int resumeId, Connection conn) throws SQLException {
        List<ResumeEducation> list = new ArrayList<>();
        String sql = "SELECT * FROM resume_education WHERE resume_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, resumeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new ResumeEducation(
                        rs.getInt("id"), resumeId,
                        rs.getString("degree"), rs.getString("institution"),
                        rs.getInt("year"), rs.getString("grade")));
            }
        }
        return list;
    }

    private List<ResumeExperience> getExperienceList(int resumeId, Connection conn) throws SQLException {
        List<ResumeExperience> list = new ArrayList<>();
        String sql = "SELECT * FROM resume_experience WHERE resume_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, resumeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new ResumeExperience(
                        rs.getInt("id"), resumeId,
                        rs.getString("company"), rs.getString("role"),
                        rs.getString("duration"), rs.getString("description")));
            }
        }
        return list;
    }

    private List<ResumeProject> getProjectList(int resumeId, Connection conn) throws SQLException {
        List<ResumeProject> list = new ArrayList<>();
        String sql = "SELECT * FROM resume_projects WHERE resume_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, resumeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new ResumeProject(
                        rs.getInt("id"), resumeId,
                        rs.getString("title"), rs.getString("description"),
                        rs.getString("technologies")));
            }
        }
        return list;
    }

    private List<ResumeSkill> getSkillList(int resumeId, Connection conn) throws SQLException {
        List<ResumeSkill> list = new ArrayList<>();
        String sql = "SELECT * FROM resume_skills WHERE resume_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, resumeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new ResumeSkill(
                        rs.getInt("id"), resumeId,
                        rs.getString("skill_name"), rs.getString("proficiency")));
            }
        }
        return list;
    }

    @Override
    public boolean updateSummary(int resumeId, String summary) {
        String sql = "UPDATE resumes SET summary = ? WHERE id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, summary);
            stmt.setInt(2, resumeId);
            boolean updated = stmt.executeUpdate() > 0;
            if (updated)
                logger.info("Updated summary for resume ID: {}", resumeId);
            return updated;
        } catch (SQLException e) {
            logger.error("Error updating summary: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteResume(int resumeId) {
        String sql = "DELETE FROM resumes WHERE id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, resumeId);
            boolean deleted = stmt.executeUpdate() > 0;
            if (deleted)
                logger.info("Deleted resume ID: {}", resumeId);
            return deleted;
        } catch (SQLException e) {
            logger.error("Error deleting resume: {}", e.getMessage());
        }
        return false;
    }

    // --- Section Management ---

    @Override
    public boolean addEducation(ResumeEducation edu) {
        String sql = "INSERT INTO resume_education (resume_id, degree, institution, year, grade) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, edu.getResumeId());
            stmt.setString(2, edu.getDegree());
            stmt.setString(3, edu.getInstitution());
            stmt.setInt(4, edu.getYear());
            stmt.setString(5, edu.getGrade());
            boolean added = stmt.executeUpdate() > 0;
            if (added)
                logger.info("Added education to resume ID: {}", edu.getResumeId());
            return added;
        } catch (SQLException e) {
            logger.error("Error adding education: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteEducation(int id) {
        return deleteSection("resume_education", id);
    }

    @Override
    public boolean addExperience(ResumeExperience exp) {
        String sql = "INSERT INTO resume_experience (resume_id, company, role, duration, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, exp.getResumeId());
            stmt.setString(2, exp.getCompany());
            stmt.setString(3, exp.getRole());
            stmt.setString(4, exp.getDuration());
            stmt.setString(5, exp.getDescription());
            boolean added = stmt.executeUpdate() > 0;
            if (added)
                logger.info("Added experience to resume ID: {}", exp.getResumeId());
            return added;
        } catch (SQLException e) {
            logger.error("Error adding experience: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteExperience(int id) {
        return deleteSection("resume_experience", id);
    }

    @Override
    public boolean addProject(ResumeProject proj) {
        String sql = "INSERT INTO resume_projects (resume_id, title, description, technologies) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, proj.getResumeId());
            stmt.setString(2, proj.getTitle());
            stmt.setString(3, proj.getDescription());
            stmt.setString(4, proj.getTechnologies());
            boolean added = stmt.executeUpdate() > 0;
            if (added)
                logger.info("Added project to resume ID: {}", proj.getResumeId());
            return added;
        } catch (SQLException e) {
            logger.error("Error adding project: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteProject(int id) {
        return deleteSection("resume_projects", id);
    }

    @Override
    public boolean addSkill(ResumeSkill skill) {
        String sql = "INSERT INTO resume_skills (resume_id, skill_name, proficiency) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, skill.getResumeId());
            stmt.setString(2, skill.getSkillName());
            stmt.setString(3, skill.getProficiency());
            boolean added = stmt.executeUpdate() > 0;
            if (added)
                logger.info("Added skill to resume ID: {}", skill.getResumeId());
            return added;
        } catch (SQLException e) {
            logger.error("Error adding skill: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteSkill(int id) {
        return deleteSection("resume_skills", id);
    }

    private boolean deleteSection(String tableName, int id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            boolean deleted = stmt.executeUpdate() > 0;
            if (deleted)
                logger.info("Deleted from {} ID: {}", tableName, id);
            return deleted;
        } catch (SQLException e) {
            logger.error("Error deleting from {}: {}", tableName, e.getMessage());
        }
        return false;
    }
}

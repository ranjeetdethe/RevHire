package com.revhire.service;

import com.revhire.dao.ResumeDAO;
import com.revhire.model.Resume;
import com.revhire.model.ResumeEducation;
import com.revhire.service.impl.ResumeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ResumeServiceTest {
    private ResumeService resumeService;
    private FakeResumeDAO fakeDAO;

    @BeforeEach
    void setUp() {
        fakeDAO = new FakeResumeDAO();
        resumeService = new ResumeServiceImpl(fakeDAO);
    }

    @Test
    void testCreateResume_Success() {
        Resume result = resumeService.createResume(1, "Test Summary");
        assertNotNull(result);
        assertEquals(1, result.getJobSeekerId());
        assertEquals("Test Summary", result.getSummary());
    }

    @Test
    void testGetResumeBySeekerId_Found() {
        fakeDAO.createResume(new Resume(1, 1, "Existing"));
        Resume result = resumeService.getResumeBySeekerId(1);
        assertNotNull(result);
        assertEquals("Existing", result.getSummary());
    }

    @Test
    void testAddEducation_ResumeExists() {
        fakeDAO.createResume(new Resume(1, 1, "Summary"));
        boolean result = resumeService.addEducation(1, new ResumeEducation("BS", "Uni", 2020, "A"));
        assertTrue(result);
    }

    @Test
    void testAddEducation_NoResume() {
        boolean result = resumeService.addEducation(99, new ResumeEducation("BS", "Uni", 2020, "A"));
        assertFalse(result);
    }

    // --- Fake DAO ---
    static class FakeResumeDAO implements ResumeDAO {
        private Resume resume;

        @Override
        public Resume createResume(Resume r) {
            r.setId(1);
            this.resume = r;
            return r;
        }

        @Override
        public Optional<Resume> getResumeBySeekerId(int jobSeekerId) {
            if (resume != null && resume.getJobSeekerId() == jobSeekerId) {
                return Optional.of(resume);
            }
            return Optional.empty();
        }

        @Override
        public boolean updateSummary(int resumeId, String summary) {
            if (resume != null && resume.getId() == resumeId) {
                resume.setSummary(summary);
                return true;
            }
            return false;
        }

        @Override
        public boolean deleteResume(int resumeId) {
            if (resume != null && resume.getId() == resumeId) {
                resume = null;
                return true;
            }
            return false;
        }

        @Override
        public boolean addEducation(ResumeEducation e) {
            return true;
        }

        @Override
        public boolean deleteEducation(int id) {
            return true;
        }

        @Override
        public boolean addExperience(com.revhire.model.ResumeExperience e) {
            return true;
        }

        @Override
        public boolean deleteExperience(int id) {
            return true;
        }

        @Override
        public boolean addProject(com.revhire.model.ResumeProject p) {
            return true;
        }

        @Override
        public boolean deleteProject(int id) {
            return true;
        }

        @Override
        public boolean addSkill(com.revhire.model.ResumeSkill s) {
            return true;
        }

        @Override
        public boolean deleteSkill(int id) {
            return true;
        }
    }
}

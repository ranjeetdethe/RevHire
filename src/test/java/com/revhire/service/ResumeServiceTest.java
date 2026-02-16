package com.revhire.service;

import com.revhire.model.JobSeeker;
import com.revhire.model.Resume;
import com.revhire.repository.JobSeekerRepository;
import com.revhire.repository.ResumeRepository;
import com.revhire.service.impl.ResumeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResumeServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private JobSeekerRepository jobSeekerRepository;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    private JobSeeker jobSeeker;

    @BeforeEach
    public void setUp() {
        jobSeeker = new JobSeeker();
        jobSeeker.setId(100);
        com.revhire.model.User user = new com.revhire.model.User();
        user.setId(5);
        jobSeeker.setUser(user);
    }

    @Test
    public void testGetResumeByUserId_Success() {
        when(jobSeekerRepository.findByUser_Id(5)).thenReturn(Optional.of(jobSeeker));

        Resume resume = new Resume();
        resume.setJobSeekerId(100);
        when(resumeRepository.findByJobSeekerId(100)).thenReturn(Optional.of(resume));

        Resume result = resumeService.getResumeByUserId(5);
        assertNotNull(result);
        assertEquals(100, result.getJobSeekerId());
    }

    @Test
    public void testSaveResumeFile_Success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn(new byte[] { 1, 2, 3 });
        when(file.getOriginalFilename()).thenReturn("test.pdf");
        when(file.getContentType()).thenReturn("application/pdf");

        when(jobSeekerRepository.findByUser_Id(5)).thenReturn(Optional.of(jobSeeker));
        when(resumeRepository.findByJobSeekerId(100)).thenReturn(Optional.empty()); // No existing resume

        assertDoesNotThrow(() -> resumeService.saveResumeFile(5, file));

        verify(resumeRepository, times(1)).save(any(Resume.class));
    }
}

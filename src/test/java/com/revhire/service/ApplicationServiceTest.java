package com.revhire.service;

import com.revhire.model.Application;
import com.revhire.model.Job;
import com.revhire.model.User;
import com.revhire.model.JobSeeker;
import com.revhire.repository.ApplicationRepository;
import com.revhire.repository.JobRepository;
import com.revhire.repository.UserRepository;
import com.revhire.repository.JobSeekerRepository;
import com.revhire.service.impl.ApplicationServiceImpl;
import com.revhire.model.Employer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.List;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobSeekerRepository jobSeekerRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private Job job;
    private User user;
    private JobSeeker jobSeeker;

    @BeforeEach
    public void setUp() {
        job = new Job();
        job.setId(10);
        job.setTitle("Software Engineer");

        user = new User();
        user.setId(2);
        user.setEmail("seeker@test.com");

        jobSeeker = new JobSeeker();
        jobSeeker.setId(5);
        jobSeeker.setUser(user);
    }

    @Test
    public void testApplyJob_Success() {
        when(jobRepository.findById(10)).thenReturn(Optional.of(job));
        when(jobSeekerRepository.findByUser_Id(2)).thenReturn(Optional.of(jobSeeker));

        // Mock Employer for notification logic
        User employerUser = new User();
        employerUser.setId(99);
        Employer employer = new Employer();
        employer.setUser(employerUser);
        job.setEmployer(employer);

        applicationService.applyJob(10, 2);

        ArgumentCaptor<Application> captor = ArgumentCaptor.forClass(Application.class);
        verify(applicationRepository).save(captor.capture());
        Application savedApp = captor.getValue();

        assertNotNull(savedApp);
        assertEquals(Application.ApplicationStatus.APPLIED, savedApp.getStatus());
        assertEquals(10, savedApp.getJob().getId());
        assertEquals(5, savedApp.getJobSeeker().getId());

        verify(notificationService).createNotification(eq(99), anyString());
    }

    @Test
    public void testGetApplicationsByUserId_Success() {
        // Use updated repository method
        when(applicationRepository.findByJobSeeker_User_Id(2)).thenReturn(List.of(new Application()));

        List<Application> apps = applicationService.getApplicationsByUserId(2);

        assertFalse(apps.isEmpty());
        assertEquals(1, apps.size());
    }
}

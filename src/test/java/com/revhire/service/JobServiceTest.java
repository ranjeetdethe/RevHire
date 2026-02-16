package com.revhire.service;

import com.revhire.model.Job;
import com.revhire.model.User;
import com.revhire.model.Employer;
import com.revhire.repository.JobRepository;
import com.revhire.service.impl.JobServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    private Job job;
    private Employer employer;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setId(1);
        user.setEmail("employer@tech.com");

        employer = new Employer();
        employer.setId(1);
        employer.setUser(user);
        employer.setCompanyName("Tech Corp");

        job = new Job();
        job.setId(101);
        job.setTitle("Senior Dev");
        job.setEmployer(employer);
    }

    @Test
    public void testPostJob_Success() {
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> i.getArguments()[0]);

        Job postedJob = jobService.postJob(job);

        assertNotNull(postedJob);
        assertNotNull(postedJob.getCreatedAt());
        assertEquals(Job.JobStatus.OPEN, postedJob.getStatus());
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    public void testGetJobsByEmployer() {
        job.setStatus(Job.JobStatus.OPEN);
        when(jobRepository.findByEmployer_Id(1)).thenReturn(List.of(job));

        List<Job> jobs = jobService.getJobsByEmployer(1);

        assertFalse(jobs.isEmpty());
        assertEquals(1, jobs.size());
        assertEquals(101, jobs.get(0).getId());
    }
}

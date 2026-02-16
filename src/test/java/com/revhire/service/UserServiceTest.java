package com.revhire.service;

import com.revhire.model.JobSeeker;
import com.revhire.model.User;
import com.revhire.repository.EmployerRepository;
import com.revhire.repository.JobSeekerRepository;
import com.revhire.repository.UserRepository;
import com.revhire.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobSeekerRepository jobSeekerRepository;

    @Mock
    private EmployerRepository employerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private JobSeeker jobSeeker;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1);
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRole(User.UserRole.JOB_SEEKER);

        jobSeeker = new JobSeeker();
        jobSeeker.setId(1);
        jobSeeker.setUser(user);
    }

    @Test
    public void testRegisterUser_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        // JobSeeker creation mocks are void or save
        // jobSeekerRepository.save() usually returns saved entity but here we don't
        // capture it strictly for simple test

        User createdUser = userService.registerUser("John", "Doe", "john@example.com", "password", "1234567890",
                User.UserRole.JOB_SEEKER, "Q", "A");

        assertNotNull(createdUser);
        assertEquals("John", createdUser.getFirstName());
        assertEquals("encodedPassword", createdUser.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jobSeekerRepository, times(1)).save(any(JobSeeker.class));
    }

    @Test
    public void testRegisterUser_EmailExists() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser("John", "Doe", "john@example.com", "password", "1234567890",
                    User.UserRole.JOB_SEEKER, "Q", "A");
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateJobSeekerProfile_Success() {
        // Prepare mock for existing
        when(jobSeekerRepository.findByUser_Id(1)).thenReturn(Optional.of(jobSeeker));

        JobSeeker updateDetails = new JobSeeker();
        updateDetails.setUser(user); // Important for finding existing
        updateDetails.setResumeText("Updated Resume");

        boolean result = userService.updateJobSeekerProfile(updateDetails);

        assertTrue(result);
        assertEquals("Updated Resume", jobSeeker.getResumeText());
        verify(jobSeekerRepository, times(1)).save(jobSeeker);
    }
}

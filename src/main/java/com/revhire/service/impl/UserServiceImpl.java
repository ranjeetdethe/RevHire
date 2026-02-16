package com.revhire.service.impl;

import com.revhire.model.Employer;
import com.revhire.model.JobSeeker;
import com.revhire.model.User;
import com.revhire.repository.EmployerRepository;
import com.revhire.repository.JobSeekerRepository;
import com.revhire.repository.UserRepository;
import com.revhire.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final EmployerRepository employerRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, JobSeekerRepository jobSeekerRepository,
            EmployerRepository employerRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jobSeekerRepository = jobSeekerRepository;
        this.employerRepository = employerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(String firstName, String lastName, String email, String password, String phone,
            User.UserRole role, String securityQuestion, String securityAnswer) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use.");
        }

        String encodedPassword = passwordEncoder.encode(password);

        User newUser = new User(firstName, lastName, email, encodedPassword, phone, role);
        newUser.setSecurityQuestion(securityQuestion);
        newUser.setSecurityAnswer(securityAnswer);

        User savedUser = userRepository.save(newUser);

        // Create corresponding profile
        if (role == User.UserRole.JOB_SEEKER) {
            JobSeeker seeker = new JobSeeker();
            seeker.setUser(savedUser);
            jobSeekerRepository.save(seeker);
        } else if (role == User.UserRole.EMPLOYER) {
            Employer employer = new Employer();
            employer.setUser(savedUser);
            employer.setCompanyName("Pending Update");
            employerRepository.save(employer);
        }

        return savedUser;
    }

    @Override
    public Optional<User> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean resetPassword(String email, String securityAnswer, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getSecurityAnswer() != null && user.getSecurityAnswer().equalsIgnoreCase(securityAnswer)) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public void createJobSeekerProfile(JobSeeker seeker) {
        jobSeekerRepository.save(seeker);
    }

    @Override
    public void createEmployerProfile(Employer employer) {
        employerRepository.save(employer);
    }

    @Override
    public Optional<JobSeeker> getJobSeekerProfile(int userId) {
        return jobSeekerRepository.findByUser_Id(userId);
    }

    @Override
    public Optional<Employer> getEmployerProfile(int userId) {
        return employerRepository.findByUser_Id(userId);
    }

    @Override
    public boolean updateJobSeekerProfile(JobSeeker seeker) {
        if (seeker.getUser() != null) {
            Optional<JobSeeker> existing = jobSeekerRepository.findByUser_Id(seeker.getUser().getId());
            if (existing.isPresent()) {
                JobSeeker existingSeeker = existing.get();
                existingSeeker.setResumeText(seeker.getResumeText());
                existingSeeker.setEducation(seeker.getEducation());
                existingSeeker.setExperience(seeker.getExperience());
                existingSeeker.setSkills(seeker.getSkills());
                existingSeeker.setCertifications(seeker.getCertifications());
                existingSeeker.setLocation(seeker.getLocation());

                User user = existingSeeker.getUser();
                if (seeker.getFirstName() != null)
                    user.setFirstName(seeker.getFirstName());
                if (seeker.getLastName() != null)
                    user.setLastName(seeker.getLastName());
                if (seeker.getPhone() != null)
                    user.setPhone(seeker.getPhone());

                jobSeekerRepository.save(existingSeeker);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateEmployerProfile(Employer employer) {
        if (employer.getUser() != null) {
            Optional<Employer> existing = employerRepository.findByUser_Id(employer.getUser().getId());
            if (existing.isPresent()) {
                Employer existingEmp = existing.get();
                existingEmp.setCompanyName(employer.getCompanyName());
                existingEmp.setIndustry(employer.getIndustry());
                existingEmp.setLocation(employer.getLocation());
                existingEmp.setDescription(employer.getDescription());

                employerRepository.save(existingEmp);
                return true;
            }
        }
        return false;
    }

    @Override
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }
}

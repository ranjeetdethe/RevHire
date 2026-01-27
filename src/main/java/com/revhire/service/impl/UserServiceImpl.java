package com.revhire.service.impl;

import com.revhire.dao.EmployerDAO;
import com.revhire.dao.JobSeekerDAO;
import com.revhire.dao.UserDAO;
import com.revhire.dao.impl.EmployerDAOImpl;
import com.revhire.dao.impl.JobSeekerDAOImpl;
import com.revhire.dao.impl.UserDAOImpl;
import com.revhire.model.Employer;
import com.revhire.model.JobSeeker;
import com.revhire.model.User;
import com.revhire.service.UserService;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final EmployerDAO employerDAO;
    private final JobSeekerDAO jobSeekerDAO;

    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
        this.employerDAO = new EmployerDAOImpl();
        this.jobSeekerDAO = new JobSeekerDAOImpl();
    }

    @Override
    public User registerUser(String firstName, String lastName, String email, String password, String phone,
            User.UserRole role, String securityQuestion, String securityAnswer) {
        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email already in use.");
        }
        User newUser = new User(firstName, lastName, email, password, phone, role);
        newUser.setSecurityQuestion(securityQuestion);
        newUser.setSecurityAnswer(securityAnswer);
        return userDAO.createUser(newUser);
    }

    @Override
    public Optional<User> login(String email, String password) {
        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean resetPassword(String email, String securityAnswer, String newPassword) {
        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getSecurityAnswer() != null && user.getSecurityAnswer().equalsIgnoreCase(securityAnswer)) {
                return userDAO.updatePassword(email, newPassword);
            }
        }
        return false;
    }

    @Override
    public void createJobSeekerProfile(JobSeeker seeker) {
        jobSeekerDAO.create(seeker);
    }

    @Override
    public void createEmployerProfile(Employer employer) {
        employerDAO.create(employer);
    }

    @Override
    public Optional<JobSeeker> getJobSeekerProfile(int userId) {
        return jobSeekerDAO.findByUserId(userId);
    }

    @Override
    public Optional<Employer> getEmployerProfile(int userId) {
        return employerDAO.findByUserId(userId);
    }

    @Override
    public boolean updateJobSeekerProfile(JobSeeker seeker) {
        return jobSeekerDAO.update(seeker);
    }

    @Override
    public boolean updateEmployerProfile(Employer employer) {
        return employerDAO.update(employer);
    }
}

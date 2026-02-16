package com.revhire.service.impl;

import com.revhire.model.Employer;
import com.revhire.repository.EmployerRepository;
import com.revhire.service.EmployerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployerServiceImpl implements EmployerService {

    private final EmployerRepository employerRepository;

    public EmployerServiceImpl(EmployerRepository employerRepository) {
        this.employerRepository = employerRepository;
    }

    @Override
    public Employer getProfileByUserId(int userId) {
        return employerRepository.findByUser_Id(userId).orElse(null);
    }

    @Override
    public Employer updateProfile(Employer employer) {
        return employerRepository.save(employer);
    }
}

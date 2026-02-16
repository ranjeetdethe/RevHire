package com.revhire.service;

import com.revhire.model.Employer;

public interface EmployerService {
    Employer getProfileByUserId(int userId);

    Employer updateProfile(Employer employer);
}

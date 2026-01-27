package com.revhire.service;

import java.util.Scanner;

public interface JobSeekerService {
    void viewProfile(int userId);

    void updateProfile(int userId, Scanner scanner);
}

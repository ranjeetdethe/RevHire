package com.revhire.ui;

import com.revhire.service.UserService;
import com.revhire.service.impl.UserServiceImpl;

public class MainMenu {
    private final UserService userService;
    private final AuthMenu authMenu;

    public MainMenu() {
        this.userService = new UserServiceImpl();
        this.authMenu = new AuthMenu(userService);
    }

    public void start() {
        while (true) {
            System.out.println("\n=== RevHire Job Portal ===");
            System.out.println("1. Login");
            System.out.println("2. Register as Job Seeker");
            System.out.println("3. Register as Employer");
            System.out.println("4. Forgot Password");
            System.out.println("5. Exit");

            int choice = InputHelper.readInt("Enter your choice");

            switch (choice) {
                case 1:
                    authMenu.login();
                    break;
                case 2:
                    authMenu.registerSeeker();
                    break;
                case 3:
                    authMenu.registerEmployer();
                    break;
                case 4:
                    authMenu.handleForgotPassword();
                    break;
                case 5:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}

package com.revhire.ui;

import java.util.Scanner;

public class InputHelper {
    private static final Scanner scanner = new Scanner(System.in);

    public static int readInt(String prompt) {
        System.out.print(prompt + ": ");
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            System.out.print(prompt + ": ");
            scanner.next(); // consume invalid input
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return value;
    }

    public static String readString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine();
    }

    public static Scanner getScanner() {
        return scanner;
    }
}

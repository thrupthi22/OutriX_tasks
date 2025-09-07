package com.bankingapp;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ATM {
    /**
     * The main method that runs the banking application.
     * It creates a BankAccount instance and starts the ATM interface.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Create a new bank account with an initial balance of $500.
        BankAccount userAccount = new BankAccount(500.00);
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        System.out.println("Welcome to the Simple Banking App!");

        // Main application loop.
        while (!exit) {
            printMenu();
            System.out.print("Please enter your choice (1-4): ");

            try {
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        // Display current balance.
                        System.out.printf("Your current balance is: $%.2f%n", userAccount.getBalance());
                        break;
                    case 2:
                        // Handle deposit.
                        System.out.print("Enter the amount to deposit: $");
                        double depositAmount = scanner.nextDouble();
                        userAccount.deposit(depositAmount);
                        break;
                    case 3:
                        // Handle withdrawal.
                        System.out.print("Enter the amount to withdraw: $");
                        double withdrawAmount = scanner.nextDouble();
                        userAccount.withdraw(withdrawAmount);
                        break;
                    case 4:
                        // Exit the application.
                        exit = true;
                        System.out.println("Thank you for using our bank. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please select a number between 1 and 4.");
                }
            } catch (InputMismatchException e) {
                // Handle non-numeric input.
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear the invalid input from the scanner.
            }
            System.out.println(); // Add a newline for better readability.
        }

        scanner.close(); // Close the scanner to prevent resource leaks.
    }

    /**
     * Prints the main menu options to the console.
     */
    public static void printMenu() {
        System.out.println("--- Main Menu ---");
        System.out.println("1. Check Balance");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Exit");
        System.out.println("-----------------");
    }
}

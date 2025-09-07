package com.bankingapp;

public class BankAccount {
    // Encapsulate the balance to protect it from direct external modification.
    private double balance;

    /**
     * Constructor to create a new bank account with an initial balance.
     * @param initialBalance The starting balance of the account.
     */
    public BankAccount(double initialBalance) {
        // Validate that the initial balance is not negative.
        if (initialBalance >= 0) {
            this.balance = initialBalance;
        } else {
            this.balance = 0;
            System.out.println("Initial balance cannot be negative. Account created with a balance of $0.00.");
        }
    }

    /**
     * Returns the current balance of the account.
     * @return The current balance.
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Deposits a specified amount into the account.
     * The amount must be a positive value.
     * @param amount The amount to deposit.
     */
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.printf("Successfully deposited: $%.2f%n", amount);
            System.out.printf("Current balance: $%.2f%n", balance);
        } else {
            System.out.println("Invalid amount. Deposit amount must be positive.");
        }
    }

    /**
     * Withdraws a specified amount from the account.
     * The amount must be positive and not exceed the current balance.
     * @param amount The amount to withdraw.
     */
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid amount. Withdrawal amount must be positive.");
        } else if (amount > balance) {
            System.out.printf("Withdrawal failed. Insufficient funds. Current balance is $%.2f%n", balance);
        } else {
            balance -= amount;
            System.out.printf("Successfully withdrew: $%.2f%n", amount);
            System.out.printf("Remaining balance: $%.2f%n", balance);
        }
    }
}
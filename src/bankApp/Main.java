
package bankApp;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import bankApp.exception.AccountNotFoundException;
import bankApp.exception.InsufficientFundsException;
import bankApp.exception.InvalidAmountException;
import bankApp.Model.BankAccount;
import bankApp.Model.Transaction;
import bankApp.Service.BankingService;
import bankApp.Storage.FileStorage;

public class Main {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            FileStorage fileStorage = new FileStorage("data");
            BankingService bankingService =
                    new BankingService(fileStorage);

            boolean running = true;

            while (running) {
                System.out.println("\n===== BANK MANAGEMENT SYSTEM =====");
                System.out.println("1. Create Account");
                System.out.println("2. Check Balance");
                System.out.println("3. Deposit");
                System.out.println("4. Withdraw");
                System.out.println("5. Transfer");
                System.out.println("6. Transaction History");
                System.out.println("7. Save Data");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");

                String choice = scanner.nextLine().trim();

                try {
                    switch (choice) {

                        case "1" -> {
                            System.out.print("Account number: ");
                            String accountNumber =
                                    scanner.nextLine().trim();

                            System.out.print("Account holder name: ");
                            String name = scanner.nextLine().trim();

                            System.out.print("Opening balance: ");
                            BigDecimal balance =
                                    new BigDecimal(scanner.nextLine().trim());

                            bankingService.createAccount(
                                    accountNumber, name, balance);

                            bankingService.saveData();
                            System.out.println("Account created successfully.");
                        }

                        case "2" -> {
                            System.out.print("Account number: ");
                            String accountNumber =
                                    scanner.nextLine().trim();

                            BankAccount account =
                                    bankingService.findAccount(accountNumber);

                            System.out.println(
                                    "Account holder: "
                                    + account.getAccountHolderName());

                            System.out.println(
                                    "Balance: "
                                    + account.getBalance().toPlainString());
                        }

                        case "3" -> {
                            System.out.print("Account number: ");
                            String accountNumber =
                                    scanner.nextLine().trim();

                            System.out.print("Deposit amount: ");
                            BigDecimal amount =
                                    new BigDecimal(scanner.nextLine().trim());

                            bankingService.deposit(accountNumber, amount);
                            bankingService.saveData();

                            System.out.println("Deposit successful.");
                        }

                        case "4" -> {
                            System.out.print("Account number: ");
                            String accountNumber =
                                    scanner.nextLine().trim();

                            System.out.print("Withdrawal amount: ");
                            BigDecimal amount =
                                    new BigDecimal(scanner.nextLine().trim());

                            bankingService.withdraw(accountNumber, amount);
                            bankingService.saveData();

                            System.out.println("Withdrawal successful.");
                        }

                        case "5" -> {
                            System.out.print("Source account number: ");
                            String fromAccount =
                                    scanner.nextLine().trim();

                            System.out.print("Target account number: ");
                            String toAccount =
                                    scanner.nextLine().trim();

                            System.out.print("Transfer amount: ");
                            BigDecimal amount =
                                    new BigDecimal(scanner.nextLine().trim());

                            bankingService.transfer(
                                    fromAccount, toAccount, amount);

                            bankingService.saveData();
                            System.out.println("Transfer successful.");
                        }

                        case "6" -> {
                            System.out.print(
                                    "Account number (leave blank for all): ");
                            String accountNumber =
                                    scanner.nextLine().trim();

                            List<Transaction> history;

                            if (accountNumber.isEmpty()) {
                                history = bankingService
                                        .getTransactionHistory();
                            } else {
                                history = bankingService
                                        .getTransactionHistory(accountNumber);
                            }

                            if (history.isEmpty()) {
                                System.out.println("No transactions found.");
                            } else {
                                for (Transaction transaction : history) {
                                    System.out.println(
                                            transaction.toDisplayString());
                                }
                            }
                        }

                        case "7" -> {
                            bankingService.saveData();
                            System.out.println("Data saved successfully.");
                        }

                        case "0" -> {
                            bankingService.saveData();
                            running = false;
                            System.out.println("Thank you for using the system.");
                        }

                        default ->
                            System.out.println(
                                    "Invalid choice. Please try again.");
                    }

                } catch (NumberFormatException exception) {
                    System.out.println(
                            "Invalid number. Please enter a valid amount.");

                } catch (AccountNotFoundException
                         | InvalidAmountException
                         | InsufficientFundsException exception) {
                    System.out.println("Operation failed: "
                            + exception.getMessage());

                } catch (IllegalArgumentException exception) {
                    System.out.println("Invalid input: "
                            + exception.getMessage());

                } catch (IOException exception) {
                    System.out.println("File operation failed: "
                            + exception.getMessage());
                }
            }

        } catch (IOException | InvalidAmountException exception) {
            System.out.println("Could not start or close the application: "
                    + exception.getMessage());
        }
    }
}

package com.example.bank_management_system;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManagerController {

    @FXML
    private Button checkAccountStatusButton;

    @FXML
    private TextField accountNumberTextField;

    @FXML
    private TextArea accountDetailsTextArea;

    private List<Account> accounts;

    @FXML
    public void initialize() {
        accountDetailsTextArea.setText("Please enter an account number to check its status.");
        accountNumberTextField.setPromptText("Enter Account Number");
        checkAccountStatusButton.setDisable(true);

        accountNumberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkAccountStatusButton.setDisable(newValue.trim().isEmpty());
        });

        try {
            accounts = loadAccountsFromCSV("src/main/resources/com/example/bank_management_system/bank_database.csv");
        } catch (IOException e) {
            e.printStackTrace();
            accountDetailsTextArea.setText("Failed to load account data.");
        }
    }

    @FXML
    private void handleCheckAccountStatusButtonAction() {
        String accountNumber = accountNumberTextField.getText();

        Account account = getAccountDetails(accountNumber);
        if (account == null) {
            accountDetailsTextArea.setText("Account not found.");
            return;
        }

        List<Transaction> transactions = getAccountTransactions(accountNumber);

        StringBuilder details = new StringBuilder();
        details.append("Account Number: ").append(account.getAccountNumber()).append("\n");
        details.append("Account Holder: ").append(account.getAccountHolderName()).append("\n");
        details.append("Balance: $").append(account.getBalance()).append("\n");
        details.append("Status: ").append(account.getStatus()).append("\n\n");
        details.append("Transactions:\n");

        for (Transaction transaction : transactions) {
            details.append(transaction.getDate())
                    .append(" - ")
                    .append(transaction.getAmount() > 0 ? "Deposit" : "Withdrawal")
                    .append(": $")
                    .append(transaction.getAmount())
                    .append("\n");
        }

        accountDetailsTextArea.setText(details.toString());
    }

    private List<Account> loadAccountsFromCSV(String filePath) throws IOException {
        List<Account> accounts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 4) {
                    String position = values[0].trim();
                    String accountNumber = values[1].trim();
                    String username = values[2].trim();
                    String password = values[3].trim();
                    String status = values.length > 4 ? values[4].trim() : "";

                    // Handle manager and teller rows
                    if ("manager".equalsIgnoreCase(position)) {
                        System.out.println("Manager: " + username + ", Status: " + status);
                        // You can handle manager-specific logic here
                    } else if ("teller".equalsIgnoreCase(position)) {
                        System.out.println("Teller: " + username + ", Status: " + status);
                        // You can handle teller-specific logic here
                    } else if ("Client".equalsIgnoreCase(position)) {
                        try {
                            double balance = 0;
                            List<Transaction> transactions = new ArrayList<>();

                            // Process transactions starting from column index 5
                            for (int i = 5; i < values.length; i += 2) {
                                if (i + 1 < values.length && !values[i].isEmpty() && !values[i + 1].isEmpty()) {
                                    try {
                                        String date = values[i].trim();
                                        double amount = Double.parseDouble(values[i + 1].trim());
                                        transactions.add(new Transaction(date, amount));
                                        balance += amount; // Accumulate balance
                                    } catch (NumberFormatException e) {
                                        System.err.println("Invalid amount format: " + values[i + 1]);
                                    }
                                }
                            }

                            Account account = new Account(accountNumber, username, status, balance);
                            for (Transaction transaction : transactions) {
                                account.addTransaction(transaction);
                            }
                            accounts.add(account);
                            System.out.println("Added client: " + username + ", status: " + status);

                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.err.println("Missing values in CSV row: " + line);
                        }
                    }
                }
            }
        }
        return accounts;
    }



    private double calculateInitialBalance(String[] values) {
        double balance = 0;
        for (int i = 6; i < values.length; i += 2) {
            if (values.length > i && !values[i].isEmpty()) {
                balance += Double.parseDouble(values[i]);
            }
        }
        return balance;
    }

    private Account getAccountDetails(String accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    private List<Transaction> getAccountTransactions(String accountNumber) {
        Account account = getAccountDetails(accountNumber);
        if (account != null) {
            return account.getTransactions();
        }
        return new ArrayList<>();
    }


    public static class Account {
        private String accountNumber;
        private String accountHolderName;
        private String status;
        private double balance;
        private List<Transaction> transactions = new ArrayList<>();

        public Account(String accountNumber, String accountHolderName, String status, double balance) {
            this.accountNumber = accountNumber;
            this.accountHolderName = accountHolderName;
            this.status = status;
            this.balance = balance;
        }

        public void addTransaction(Transaction transaction) {
            transactions.add(transaction);
        }

        public String getAccountNumber() { return accountNumber; }
        public String getAccountHolderName() { return accountHolderName; }
        public String getStatus() { return status; }
        public double getBalance() { return balance; }
        public List<Transaction> getTransactions() { return transactions; }
    }

    public static class Transaction {
        private String date;
        private double amount;

        public Transaction(String date, double amount) {
            this.date = date;
            this.amount = amount;
        }

        public String getDate() { return date; }
        public double getAmount() { return amount; }
    }
}

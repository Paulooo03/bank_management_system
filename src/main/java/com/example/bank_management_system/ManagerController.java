package com.example.bank_management_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.collections.ObservableList;

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
    private Label accountNumberLabel;

    @FXML
    private Label accountHolderLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private Label statusLabel;

    private List<Account> accounts;

    @FXML
    public void initialize() {
        clearLabels();
        accountNumberTextField.setPromptText("Enter Account Number");
        checkAccountStatusButton.setDisable(true);

        accountNumberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkAccountStatusButton.setDisable(newValue.trim().isEmpty());
        });

        accounts = new ArrayList<>();
        try {
            // Load accounts from both CSV files
            accounts.addAll(loadAccountsFromCSV("src/main/resources/com/example/bank_management_system/manager.csv", "manager"));
            accounts.addAll(loadAccountsFromCSV("src/main/resources/com/example/bank_management_system/bank_database.csv", "client"));
            System.out.println("Total accounts loaded: " + accounts.size()); // Debug statement
        } catch (IOException e) {
            e.printStackTrace();
            accountNumberLabel.setText("Failed to load account data.");
        }
    }

    @FXML
    private void handleCheckAccountStatusButtonAction() {
        String accountNumber = accountNumberTextField.getText().trim();
        Account account = getAccountDetails(accountNumber);
        if (account == null) {
            clearLabels();
            accountNumberLabel.setText("Account not found.");
            return;
        }

        // Load the balance from the CSV file
        double balance = loadAccountBalance(accountNumber);

        accountNumberLabel.setText(account.getAccountNumber());
        accountHolderLabel.setText(account.getAccountHolderName());
        balanceLabel.setText(String.format("PHP %.2f", balance));
        statusLabel.setText(account.getStatus());
    }

    private double loadAccountBalance(String accountNumber) {
        double balance = 0.0;
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                // Ensure the row corresponds to the client account
                if (values.length > 5 && "Client".equalsIgnoreCase(values[0]) && values[1].trim().equals(accountNumber)) {
                    // Start reading transactions from index 5
                    for (int i = 5; i < values.length; i += 3) {
                        if (i + 1 < values.length) { // Ensure there's enough data for Date and Amount
                            try {
                                double amount = Double.parseDouble(values[i + 1].trim());
                                balance += amount; // Update balance with the transaction amount
                            } catch (NumberFormatException e) {
                                System.err.println("Failed to parse amount: " + values[i + 1].trim());
                            }
                        }
                    }
                    break; // Break the loop once the account is found
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error reading account balance from file.");
        }

        return balance;
    }

    private void clearLabels() {
        accountNumberLabel.setText("");
        accountHolderLabel.setText("");
        balanceLabel.setText("");
        statusLabel.setText("");
    }

    private List<Account> loadAccountsFromCSV(String filePath, String accountType) throws IOException {
        List<Account> accounts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("Reading line: " + line); // Debug statement
                String[] values = line.split(",");
                if (values.length >= 5) { // Adjusted to match minimum expected columns
                    String accountNumber = values[1].trim();
                    String username = values[2].trim();
                    String password = values[3].trim();
                    String status = values[4].trim();
                    double balance = 0.0;

                    if (values.length > 5 && isNumeric(values[5].trim())) {
                        balance = Double.parseDouble(values[5].trim());
                    }

                    if ("manager".equalsIgnoreCase(accountType) && "manager".equalsIgnoreCase(values[0].trim())) {
                        Account managerAccount = new Account(accountNumber, username, status, balance);
                        accounts.add(managerAccount);
                        System.out.println("Manager added: " + username + ", Status: " + status + ", Account Number: " + accountNumber);
                    } else if ("client".equalsIgnoreCase(accountType) && "client".equalsIgnoreCase(values[0].trim())) {
                        Account clientAccount = new ClientAccount(accountNumber, username, status, balance);
                        accounts.add(clientAccount);
                        System.out.println("Client added: " + username + ", Status: " + status + ", Account Number: " + accountNumber);
                    } else {
                        System.out.println("Skipping unknown account type.");
                    }
                } else {
                    System.out.println("Line does not have enough columns: " + line);
                }
            }
        }
        return accounts;
    }

    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private Account getAccountDetails(String accountNumber) {
        for (Account account : accounts) {
            System.out.println("Checking account: " + account.getAccountNumber()); // Debug statement
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    public static class Account {
        private String accountNumber;
        private String accountHolderName;
        private String status;
        private double balance;

        public Account(String accountNumber, String accountHolderName, String status, double balance) {
            this.accountNumber = accountNumber;
            this.accountHolderName = accountHolderName;
            this.status = status;
            this.balance = balance;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getAccountHolderName() {
            return accountHolderName;
        }

        public String getStatus() {
            return status;
        }

        public double getBalance() {
            return balance;
        }
    }

    public static class ClientAccount extends Account {
        public ClientAccount(String accountNumber, String accountHolderName, String status, double balance) {
            super(accountNumber, accountHolderName, status, balance);
        }
    }

    public void handleBackButton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.example.bank_management_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

        accounts = new ArrayList<>();
        try {
            accounts.addAll(loadAccountsFromCSV("src/main/resources/com/example/bank_management_system/manager.csv", "manager"));
            accounts.addAll(loadAccountsFromCSV("src/main/resources/com/example/bank_management_system/bank_database.csv", "client"));
            System.out.println("Total accounts loaded: " + accounts.size()); // Debug statement
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

        StringBuilder details = new StringBuilder();
        details.append("Account Number: ").append(account.getAccountNumber()).append("\n");
        details.append("Account Holder: ").append(account.getAccountHolderName()).append("\n");
        details.append("Balance: $").append(account.getBalance()).append("\n");
        details.append("Status: ").append(account.getStatus()).append("\n\n");

        accountDetailsTextArea.setText(details.toString());
    }

    private List<Account> loadAccountsFromCSV(String filePath, String accountType) throws IOException {
        List<Account> accounts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("Reading line: " + line); // Debug statement
                String[] values = line.split(",");
                if (values.length > 4) {
                    String accountNumber = values[1].trim();
                    String username = values[2].trim();
                    String password = values[3].trim();
                    String status = values.length > 4 ? values[4].trim() : "";
                    double balance = 0.0;

                    if (values.length > 5 && isNumeric(values[5].trim())) {
                        balance = Double.parseDouble(values[5].trim());
                    }

                    if ("manager".equalsIgnoreCase(accountType) && "manager".equalsIgnoreCase(values[0].trim())) {
                        Account managerAccount = new Account(accountNumber, username, status, balance);
                        accounts.add(managerAccount);
                        System.out.println("Manager added: " + username + ", Status: " + status + ", Account Number: " + accountNumber);
                    } else if ("client".equalsIgnoreCase(accountType)) {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user_selection.fxml"));
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

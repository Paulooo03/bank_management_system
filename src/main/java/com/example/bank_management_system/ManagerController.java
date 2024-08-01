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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ManagerController {

    @FXML
    private Button activateOrDeactivate;
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
    private static final String CSV_FILE_PATH = "src/main/resources/com/example/bank_management_system/bank_database.csv";

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
            accounts.addAll(loadAccountsFromCSV(CSV_FILE_PATH, "client"));
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
            activateOrDeactivate.setVisible(false); // Hide button if account is not found
            return;
        }

        double balance = loadAccountBalance(accountNumber);

        accountNumberLabel.setText(account.getAccountNumber());
        accountHolderLabel.setText(account.getAccountHolderName());
        balanceLabel.setText(String.format("PHP %.2f", balance));
        statusLabel.setText(account.getStatus());

        // Update the button label and make it visible
        if ("Active".equalsIgnoreCase(account.getStatus())) {
            activateOrDeactivate.setText("Deactivate");
        } else {
            activateOrDeactivate.setText("Activate");
        }
        activateOrDeactivate.setVisible(true); // Show button when account is found
    }

    private double loadAccountBalance(String accountNumber) {
        double balance = 0.0;

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 5 && "Client".equalsIgnoreCase(values[0]) && values[1].trim().equals(accountNumber)) {
                    for (int i = 5; i < values.length; i += 3) {
                        if (i + 1 < values.length) {
                            try {
                                double amount = Double.parseDouble(values[i + 1].trim());
                                balance += amount;
                            } catch (NumberFormatException e) {
                                System.err.println("Failed to parse amount: " + values[i + 1].trim());
                            }
                        }
                    }
                    break;
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
                String[] values = line.split(",");
                if (values.length >= 5) {
                    String accountNumber = values[1].trim();
                    String username = values[2].trim();
                    String status = values[4].trim();
                    double balance = 0.0;

                    if (values.length > 5 && isNumeric(values[5].trim())) {
                        balance = Double.parseDouble(values[5].trim());
                    }

                    if ("client".equalsIgnoreCase(accountType) && "Client".equalsIgnoreCase(values[0].trim())) {
                        Account clientAccount = new ClientAccount(accountNumber, username, status, balance);
                        accounts.add(clientAccount);
                    }
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
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    @FXML
    public void activateOrDeactivate(ActionEvent actionEvent) {
        String accountNumber = accountNumberTextField.getText().trim();
        Account account = getAccountDetails(accountNumber);

        if (account == null) {
            accountNumberLabel.setText("Account not found.");
            return;
        }

        String newStatus = "Active".equalsIgnoreCase(account.getStatus()) ? "Inactive" : "Active";
        account.setStatus(newStatus);

        try {
            List<String> lines = Files.readAllLines(Paths.get(CSV_FILE_PATH));
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CSV_FILE_PATH))) {
                for (String line : lines) {
                    String[] values = line.split(",");
                    if (values.length >= 5 && values[1].trim().equals(accountNumber)) {
                        values[4] = newStatus;
                        line = String.join(",", values);
                    }
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            accountNumberLabel.setText("Failed to update account status.");
            return;
        }

        statusLabel.setText(newStatus);
        activateOrDeactivate.setText("Active".equalsIgnoreCase(newStatus) ? "Deactivate" : "Activate");
    }

    public void handleBackButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        public void setStatus(String status) {
            this.status = status;
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
}

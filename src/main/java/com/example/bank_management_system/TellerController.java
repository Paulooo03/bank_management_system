package com.example.bank_management_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class TellerController {

    @FXML
    private TextField accountNumberTextField;
    @FXML
    private Button viewTransactionHistoryButton;
    @FXML
    private Button checkAccountDetailsButton;
    @FXML
    private Button transferMoneyButton;

    @FXML
    private void handleViewTransactionHistory(ActionEvent event) {
        String accountNumber = accountNumberTextField.getText().trim();
        if (accountNumber.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Please enter an account number.");
            return;
        }

        // Load transaction history
        UserTransactionHistoryController controller = new UserTransactionHistoryController();
        ObservableList<UserTransactionHistoryController.Transaction> transactions = controller.loadTransactions(accountNumber);

        if (transactions.isEmpty()) {
            showAlert(AlertType.INFORMATION, "Transaction History", "No transactions found for account number: " + accountNumber);
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("user_transaction_history.fxml"));
                Parent root = loader.load();

                // Get the controller for the user_transaction_history.fxml
                UserTransactionHistoryController historyController = loader.getController();
                historyController.setTransactions(transactions);

                // Show the transaction history view
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                currentStage.setScene(scene);
                currentStage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Error", "Failed to load transaction history view.");
            }
        }
    }


    @FXML
    private void handleCheckAccountDetails(ActionEvent event) {
        String accountNumber = accountNumberTextField.getText().trim();
        if (accountNumber.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Please enter an account number.");
            return;
        }

        try {
            Account account = loadAccountDetails(accountNumber);
            if (account != null) {
                String details = String.format("Account Number: %s\nAccount Holder: %s\nStatus: %s\nBalance: $%.2f",
                        account.getAccountNumber(), account.getAccountHolderName(), account.getStatus(), account.getBalance());
                showAlert(AlertType.INFORMATION, "Account Details", details);
            } else {
                showAlert(AlertType.ERROR, "Error", "Account not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to load account details.");
        }
    }

    @FXML
    private void handleTransferMoney(ActionEvent event) {
        String accountNumber = accountNumberTextField.getText().trim();
        if (accountNumber.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Please enter an account number.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Transfer Money");
        dialog.setHeaderText("Enter amount and operation");
        dialog.setContentText("Amount and operation (e.g., '100 Transfer' or '50 Withdrawal'):");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String[] input = result.get().split(" ");
            if (input.length != 2) {
                showAlert(AlertType.ERROR, "Error", "Invalid input. Please enter 'amount operation'.");
                return;
            }

            try {
                double amount = Double.parseDouble(input[0]);
                String operation = input[1].trim().toLowerCase();
                if (!operation.equals("transfer") && !operation.equals("withdrawal")) {
                    showAlert(AlertType.ERROR, "Error", "Invalid operation. Please enter 'Transfer' or 'Withdrawal'.");
                    return;
                }

                // Update CSV
                updateAccountBalance(accountNumber, amount, operation);

                showAlert(AlertType.INFORMATION, "Transfer Successful",
                        String.format("%s $%.2f for account number: %s",
                                operation.substring(0, 1).toUpperCase() + operation.substring(1), amount, accountNumber));

            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Error", "Invalid amount entered.");
            } catch (IOException e) {
                showAlert(AlertType.ERROR, "Error", "Failed to update account balance.");
                e.printStackTrace();
            }
        }
    }



    @FXML
    private void handleBackButton(ActionEvent event) {
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

    private Account loadAccountDetails(String accountNumber) throws IOException {
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 5 && "Client".equalsIgnoreCase(values[0])) {
                    String accNum = values[1].trim();
                    if (accNum.equals(accountNumber)) {
                        String accountHolderName = values[2].trim();
                        String status = values[4].trim();
                        double balance = Double.parseDouble(values[3].trim());
                        return new Account(accNum, accountHolderName, status, balance);
                    }
                }
            }
        }
        return null;
    }

    private void updateAccountBalance(String accountNumber, double amount, String operation) throws IOException {
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";
        String tempFilePath = "src/main/resources/com/example/bank_management_system/temp_bank_database.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFilePath))) {

            String line;
            boolean accountUpdated = false;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 5 && "Client".equalsIgnoreCase(values[0]) && values[1].trim().equals(accountNumber)) {
                    double balance = Double.parseDouble(values[3].trim());
                    if (operation.equals("transfer")) {
                        balance += amount; // Assuming money is added to the account
                    } else if (operation.equals("withdrawal")) {
                        balance -= amount; // Assuming money is withdrawn from the account
                    }
                    values[3] = String.valueOf(balance); // Update balance
                    line = String.join(",", values);
                    accountUpdated = true;
                }
                bw.write(line);
                bw.newLine();
            }

            if (!accountUpdated) {
                throw new IOException("Account not found for update.");
            }
        }

        // Replace old file with updated file
        Files.move(Paths.get(tempFilePath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
}

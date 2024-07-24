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
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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

        // Implement logic to display transactions (e.g., open a new stage with transaction details)
        // For example, you could load another FXML file or show a dialog
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

        // Implement logic to transfer money here
        // This would likely involve getting the amount to transfer and processing the transfer
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user_selection.fxml"));
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

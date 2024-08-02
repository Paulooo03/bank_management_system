package com.example.bank_management_system;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ClientController {

    @FXML
    private TextField accountNumberInput;

    @FXML
    private TextField passwordInput;

    @FXML
    public void onBackClick(ActionEvent actionEvent) {
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

    @FXML
    public void onClickLogin(ActionEvent actionEvent) {
        String accountNumber = accountNumberInput.getText().trim();
        String password = passwordInput.getText().trim();

        if (validateUser(accountNumber, password)) {
            showUserTransactionHistory(accountNumber);
        } else {
            // Handle invalid login
            System.out.println("Invalid login credentials.");
            // You can also display an alert to the user if needed.
        }
    }

    private boolean validateUser(String accountNumber, String password) {
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4) {
                    String storedAccountNumber = values[1].trim();
                    String storedPassword = values[3].trim(); // Assuming password is in the 4th column
                    if (storedAccountNumber.equals(accountNumber) && storedPassword.equals(password)) {
                        return true; // Valid user
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Invalid user
    }

    private void showUserTransactionHistory(String accountNumber) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user_transaction_history.fxml"));
            Parent root = loader.load();

            UserTransactionHistoryController controller = loader.getController();
            ObservableList<UserTransactionHistoryController.Transaction> transactions = controller.loadTransactions(accountNumber);
            String clientName = getClientName(accountNumber); // Get client name based on account number

            // Set transactions and client name in the controller
            controller.setTransactions(transactions, clientName);

            Scene scene = new Scene(root);
            Stage currentStage = (Stage) accountNumberInput.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getClientName(String accountNumber) {
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4) {
                    String storedAccountNumber = values[1].trim();
                    if (storedAccountNumber.equals(accountNumber)) {
                        return values[2].trim(); // Assuming client name is in the 3rd column
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown"; // Return a default value if the client name is not found
    }

    public static class Transaction {
        private final String date;
        private final String type;
        private final double amount;

        public Transaction(String date, double amount, String type) {
            this.date = date;
            this.amount = amount;
            this.type = type;
        }

        public String getDate() {
            return date;
        }

        public double getAmount() {
            return amount;
        }

        public double getBalance() { return amount;}
    }
}

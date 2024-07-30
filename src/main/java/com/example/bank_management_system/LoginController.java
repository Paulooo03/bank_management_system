package com.example.bank_management_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginController {

    @FXML
    private TextField accountNumberField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button debugButton;

    private List<Account> accounts;

    @FXML
    public void initialize() {
        accounts = new ArrayList<>();
        try {
            accounts.addAll(loadAccountsFromCSV("src/main/resources/com/example/bank_management_system/bank_database.csv"));
            System.out.println("Total accounts loaded: " + accounts.size()); // Debug statement
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load account data.");
        }

        loginButton.setOnAction(this::handleLoginButtonAction);
        debugButton.setOnAction(this::handleDebugButtonAction); // Corrected method reference
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String accountNumber = accountNumberField.getText().trim();
        String password = passwordField.getText().trim();

        Account account = validateLogin(accountNumber, password);
        if (account == null) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid account number or password. Please try again.");
            return;
        }

        // Check the position of the account and navigate accordingly
        switch (account.getPosition().toLowerCase()) {
            case "manager":
                navigateToManagerScreen(event);
                break;
            case "teller":
                navigateToTellerScreen(event);
                break;
            default:
                showUserTransactionHistory(accountNumber, event);
                break;
        }
    }

    @FXML
    private void handleDebugButtonAction(ActionEvent event) { // Renamed method
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user_selection.fxml")));
            Scene scene = new Scene(root, 1280, 720);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Le Bank Management System");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load user selection screen.");
        }
    }

    private List<Account> loadAccountsFromCSV(String filePath) throws IOException {
        List<Account> accounts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4) {
                    String position = values[0].trim();
                    String accountNumber = values[1].trim();
                    String username = values[2].trim();
                    String password = values[3].trim();
                    String status = values.length > 4 ? values[4].trim() : "";

                    Account account = new Account(position, accountNumber, username, password, status);
                    accounts.add(account);
                }
            }
        }
        return accounts;
    }

    private Account validateLogin(String accountNumber, String password) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber) && account.getPassword().equals(password)) {
                return account;
            }
        }
        return null;
    }

    private void navigateToManagerScreen(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("manager.fxml")));
            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Manager Dashboard");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load manager dashboard.");
        }
    }

    private void navigateToTellerScreen(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("teller.fxml")));
            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Teller Dashboard");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load teller dashboard.");
        }
    }

    private void showUserTransactionHistory(String accountNumber, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user_transaction_history.fxml"));
            Parent root = loader.load();

            UserTransactionHistoryController controller = loader.getController();
            ObservableList<UserTransactionHistoryController.Transaction> transactions = loadTransactions(accountNumber);

            // You need to pass both transactions and clientName
            String clientName = getClientNameFromAccount(accountNumber);
            controller.setTransactions(transactions, clientName);

            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Transaction History");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load transaction history.");
        }
    }

    private String getClientNameFromAccount(String accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account.getUsername(); // or however you want to retrieve the client name
            }
        }
        return "Unknown"; // Default name if not found
    }


    private ObservableList<UserTransactionHistoryController.Transaction> loadTransactions(String accountNumber) {
        ObservableList<UserTransactionHistoryController.Transaction> transactions = FXCollections.observableArrayList();
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4 && "Client".equalsIgnoreCase(values[0]) && values[1].trim().equals(accountNumber)) {
                    for (int i = 5; i < values.length; i += 2) {
                        String date = values[i].trim();
                        double amount = Double.parseDouble(values[i + 1].trim());
                        double balance = transactions.isEmpty() ? amount : transactions.get(transactions.size() - 1).getBalance() + amount;
                        transactions.add(new UserTransactionHistoryController.Transaction(date, amount, balance));
                    }
                    break; // Assuming account numbers are unique, stop after finding the correct account
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Account {
        private String position;
        private String accountNumber;
        private String username;
        private String password;
        private String status;

        public Account(String position, String accountNumber, String username, String password, String status) {
            this.position = position;
            this.accountNumber = accountNumber;
            this.username = username;
            this.password = password;
            this.status = status;
        }

        public String getPosition() {
            return position;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getStatus() {
            return status;
        }
    }
}

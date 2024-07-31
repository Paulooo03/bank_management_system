package com.example.bank_management_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public class UserTransactionHistoryController {

    public TextField sendMoney;
    public Button sendMoneyButton;
    public TextField sendMoneyAccount;
    public TextField requestMoney;
    public Button requestMoneyButton;
    public Text savings;
    private String currentClientAccountNumber;
    @FXML
    private Text clientName;
    @FXML
    private Text balance;
    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<Transaction, String> dateColumn;
    @FXML
    private TableColumn<Transaction, Double> amountColumn;
    @FXML
    private TableColumn<Transaction, String> descriptionColumn;
    @FXML
    private TableColumn<Transaction, Double> balanceColumn;

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
    }

    public void setTransactions(ObservableList<Transaction> transactions, String clientName) {
        transactionTable.setItems(transactions);
        setClientName(clientName);
        double latestBalance = transactions.isEmpty() ? 0.0 : transactions.get(transactions.size() - 1).getBalance();
        setBalance(latestBalance);
    }

    public ObservableList<Transaction> loadTransactions(String accountNumber) {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";
        double latestBalance = 0.0;
        String clientName = "Unknown";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                // Check if the line corresponds to a client and matches the account number
                if (values.length >= 6 && "Client".equalsIgnoreCase(values[0]) && values[1].trim().equals(accountNumber)) {
                    clientName = values[2].trim(); // Assuming the username is in the 3rd column
                    latestBalance = Double.parseDouble(values[5].trim()); // Initial balance

                    // Start parsing transactions from the last index (after Status, Date, Amount)
                    for (int i = values.length - 1; i >= 6; i -= 3) { // Decrement by 3 for Date, Amount, Description
                        if (i - 2 >= 6) { // Ensure there's a date, amount, and description
                            String date = values[i].trim(); // Date
                            double amount;
                            try {
                                amount = Double.parseDouble(values[i - 1].trim()); // Amount
                            } catch (NumberFormatException e) {
                                System.out.println("Error parsing amount: " + values[i - 1].trim());
                                continue; // Skip this transaction if there's an error
                            }
                            String description = values[i - 2].trim(); // Description
                            latestBalance += amount; // Update the running balance
                            transactions.add(new Transaction(date, amount, description, latestBalance));
                        }
                    }
                    break; // Stop after processing the correct account
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public void setClientName(String name) {
        clientName.setText("Hello, " + name);
    }

    public void setBalance(double balanceAmount) {
        balance.setText("PHP " + String.format("%.2f", balanceAmount));
    }

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
    public void handleExportCSV(ActionEvent actionEvent) {
        ObservableList<Transaction> transactions = transactionTable.getItems();
        if (transactions == null || transactions.isEmpty()) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transaction History");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Date,Amount,Description,Balance\n");
                for (Transaction transaction : transactions) {
                    writer.write(String.format("%s,%.2f,%s,%.2f\n",
                            transaction.getDate(), transaction.getAmount(), transaction.getDescription(), transaction.getBalance()));
                }
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMoney(ActionEvent actionEvent) {
    }

    @FXML
    public void sendMoneyButton(ActionEvent actionEvent) {
        String recipientAccountNumber = sendMoneyAccount.getText().trim();
        String amountStr = sendMoney.getText().trim();

        System.out.println("Recipient Account Number: " + recipientAccountNumber);
        System.out.println("Amount: " + amountStr);

        if (recipientAccountNumber.isEmpty() || amountStr.isEmpty()) {
            showAlert("Error", "Please enter both account number and amount.");
            return;
        }

        if (currentClientAccountNumber == null) {
            showAlert("Error", "Current client account number is not set.");
            return;
        }

        // Validate the amountStr before parsing
        if (!isValidAmount(amountStr)) {
            System.out.println("Invalid amount: " + amountStr); // Debugging information
            showAlert("Error", "Invalid amount. Please enter a valid number.");
            return;
        }

        // Now you can safely parse it
        double amount = Double.parseDouble(amountStr);
        if (amount <= 0) {
            showAlert("Error", "Please enter a positive amount.");
            return;
        }

        if (performTransaction(currentClientAccountNumber, recipientAccountNumber, amount)) {
            showAlert("Success", "Money sent successfully!");
            updateTransactionHistory();
        } else {
            showAlert("Error", "Failed to send money. Please check the account number and try again.");
        }
    }

    // Function to validate if the amount string is a valid number
    private boolean isValidAmount(String amountStr) {
        return amountStr.matches("\\d+(\\.\\d+)?"); // Matches positive numbers, e.g., "100", "100.00"
    }

    private boolean performTransaction(String senderAccountNumber, String recipientAccountNumber, double amount) {
        System.out.println("Sender Account Number: " + senderAccountNumber);
        System.out.println("Recipient Account Number: " + recipientAccountNumber);

        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";
        String tempFilePath = "src/main/resources/com/example/bank_management_system/temp_bank_database.csv";
        boolean senderUpdated = false;
        boolean recipientUpdated = false;
        double currentBalance = 0.0; // Initialize current balance

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 5 && "Client".equalsIgnoreCase(values[0])) {
                    String accNumber = values[1].trim();
                    if (accNumber.equals(senderAccountNumber)) {
                        currentBalance = getCurrentBalance(values); // Calculate the current balance
                        if (currentBalance >= amount) {
                            line = updateAccountLine(line, -amount, "Send");
                            senderUpdated = true;
                        } else {
                            System.out.println("Insufficient balance for sender: " + senderAccountNumber);
                            showAlert("Error", "This is beyond your current balance."); // Show alert for insufficient funds
                            return false; // Return false since the transaction can't be processed
                        }
                    } else if (accNumber.equals(recipientAccountNumber)) {
                        line = updateAccountLine(line, amount, "Receive");
                        recipientUpdated = true;
                    }
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (senderUpdated && recipientUpdated) {
            try {
                Files.move(Paths.get(tempFilePath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        if (!senderUpdated) {
            System.out.println("Failed to update sender account: " + senderAccountNumber);
        }
        if (!recipientUpdated) {
            System.out.println("Failed to update recipient account: " + recipientAccountNumber);
        }

        return false;
    }

    private double getCurrentBalance(String[] values) {
        double balance = 0.0; // Initialize balance

        // Starting from the 6th index, we want to parse transactions in the format:
        // Date, Amount, Description (which are stored in groups of three)
        for (int i = 5; i < values.length; i++) {
            if ((i - 5) % 3 == 1) { // Amount is at index 6, 9, 12, ...
                try {
                    double amount = Double.parseDouble(values[i].trim());
                    balance += amount; // Accumulate the balance
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing amount: " + values[i].trim()); // Debugging
                }
            }
        }

        return balance; // Return the calculated balance
    }

    private String updateAccountLine(String line, double amount, String description) {
        String[] values = line.split(",");
        String date = LocalDate.now().toString();

        // Create the new transaction string
        String transactionString = String.join(",", date, String.valueOf(amount), description);

        // Append the transaction details to the existing line
        return line + "," + transactionString;
    }

    private void updateTransactionHistory() {
        ObservableList<Transaction> transactions = loadTransactions(currentClientAccountNumber);
        setTransactions(transactions, getUsernameByAccountNumber(currentClientAccountNumber));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setCurrentClientAccountNumber(String accountNumber) {
        this.currentClientAccountNumber = accountNumber;
    }

    private String getUsernameByAccountNumber(String accountNumber) {
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                // Check if the line corresponds to a client and matches the account number
                if (values.length >= 6 && "Client".equalsIgnoreCase(values[0]) && values[1].trim().equals(accountNumber)) {
                    return values[2].trim(); // Return the username
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Account not found
    }

    public void sendMoneyAccount(ActionEvent actionEvent) {
    }

    public void requestMoney(ActionEvent actionEvent) {
    }

    public void requestMoneyButton(ActionEvent actionEvent) {
        String amountStr = requestMoney.getText().trim();

        System.out.println("Requested Amount: " + amountStr);

        if (amountStr.isEmpty()) {
            showAlert("Error", "Please enter an amount to request.");
            return;
        }

        if (currentClientAccountNumber == null) {
            showAlert("Error", "Current client account number is not set.");
            return;
        }

        // Validate the amountStr before parsing
        if (!isValidAmount(amountStr)) {
            System.out.println("Invalid amount: " + amountStr); // Debugging information
            showAlert("Error", "Invalid amount. Please enter a valid number.");
            return;
        }

        // Parse the requested amount
        double amount = Double.parseDouble(amountStr);
        if (amount <= 0) {
            showAlert("Error", "Please enter a positive amount.");
            return;
        }

        // Process the money request
        if (processRequestMoney(currentClientAccountNumber, amount)) {
            showAlert("Success", "Money requested successfully!");
            updateTransactionHistory(); // Update the transaction history after the request
        } else {
            showAlert("Error", "Failed to request money. Please try again.");
        }
    }

    private boolean processRequestMoney(String accountNumber, double amount) {
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";
        String tempFilePath = "src/main/resources/com/example/bank_management_system/temp_bank_database.csv";
        boolean accountUpdated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 5 && "Client".equalsIgnoreCase(values[0])) {
                    String accNumber = values[1].trim();
                    if (accNumber.equals(accountNumber)) {
                        line = updateAccountLine(line, amount, "Loaned Money"); // Update the account line with the requested amount
                        accountUpdated = true;
                    }
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // If the account was updated, replace the original file with the temporary file
        if (accountUpdated) {
            try {
                Files.move(Paths.get(tempFilePath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false; // Return false if no account was updated
    }

    public static class Transaction {
        private final String date;
        private final double amount;
        private final String description;
        private final double balance;

        public Transaction(String date, double amount, String description, double balance) {
            this.date = date;
            this.amount = amount;
            this.description = description;
            this.balance = balance;
        }

        public String getDate() {
            return date;
        }

        public double getAmount() {
            return amount;
        }

        public String getDescription() {
            return description;
        }

        public double getBalance() {
            return balance;
        }
    }
}

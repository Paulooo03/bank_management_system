package com.example.bank_management_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Optional;

public class TellerController {

    public TextArea transactionHistoryTextArea;
    public Button viewClientButton;
    @FXML
    private TextField accountNumberTextField;
    @FXML
    private Button transferMoneyButton;
    @FXML
    private Label outputLabel;

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
        dialog.setContentText("Amount and operation (e.g.,'50 Transfer' or '50 Deposit or 50 Loan' or '50 Withdrawal'):");


        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String input = result.get().trim(); // Trim the input
            System.out.println("User input: '" + input + "'"); // Debug: Print the raw input
            String[] parts = input.split("\\s+"); // Split on one or more spaces

            // Debug: Print the parts
            System.out.println("Split parts: " + Arrays.toString(parts));

            if (parts.length != 2) {
                showAlert(AlertType.ERROR, "Error", "Invalid input. Please enter in the format 'amount operation'. Example: '100 Transfer' or '50 Withdrawal'.");
                return;
            }

            try {
                // Ensure no extra spaces around the amount string
                String amountString = parts[0].trim(); // Trim the amount string
                System.out.println("Amount string: '" + amountString + "'"); // Debug: Print the trimmed amount string

                double amount = Double.parseDouble(amountString); // Try to parse the amount
                String operation = parts[1].trim().toLowerCase();

                // Validate the operation and amount based on the operation type
                if (operation.equals("transfer") || operation.equals("loan") || operation.equals("deposit")) {
                    if (amount <= 0) {
                        showAlert(AlertType.ERROR, "Error", "Amount must be positive for Transfer, Loan, or Deposit.");
                        return;
                    }
                } else if (operation.equals("withdrawal")) {
                    if (amount >= 0) {
                        showAlert(AlertType.ERROR, "Error", "Amount must be negative for Withdrawal.");
                        return;
                    }
                } else {
                    showAlert(AlertType.ERROR, "Error", "Invalid operation. Please enter 'Transfer', 'Loan', 'Deposit', or 'Withdrawal'.");
                    return;
                }

                // Update CSV
                updateAccountBalance(accountNumber, amount, operation);

                showAlert(AlertType.INFORMATION, "Transfer Successful",
                        String.format("%s $%.2f for account number: %s",
                                operation.substring(0, 1).toUpperCase() + operation.substring(1), amount, accountNumber));

            } catch (NumberFormatException e) {
                // Add debug output for the exception
                System.err.println("Failed to parse amount: " + e.getMessage());
                showAlert(AlertType.ERROR, "Error", "Invalid amount entered. Please enter a numeric value for the amount.");
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
                        String status = values[4].trim(); // Add this line to get the status
                        double balance = 0.0;

                        // Process transactions, starting from the Date after Status
                        for (int i = 5; i < values.length; i++) {
                            if (i + 1 < values.length) {
                                String date = values[i].trim();
                                String amountString = values[i + 1].trim();
                                try {
                                    double amount = Double.parseDouble(amountString);
                                    balance += amount;
                                } catch (NumberFormatException e) {
                                    System.err.println("Failed to parse amount: For input string: \"" + amountString + "\"");
                                }
                                i += 2; // Move past Date and Amount
                            }
                        }

                        return new Account(accNum, accountHolderName, status, balance); // Include status here
                    }
                }
            }
        }
        return null;
    }

    private void updateAccountBalance(String accountNumber, double amount, String operation) throws IOException {
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";
        String tempFilePath = "src/main/resources/com/example/bank_management_system/temp_bank_database.csv";

        String currentDate = java.time.LocalDate.now().toString(); // Get today's date

        try (BufferedReader br = new BufferedReader(new FileReader(filePath));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFilePath))) {

            String line;
            boolean accountUpdated = false;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                // Skip all columns before "Status"
                if (values.length >= 5 && "Client".equalsIgnoreCase(values[0]) && values[1].trim().equals(accountNumber)) {
                    double balance = 0.0;

                    // Process existing transactions
                    for (int i = 5; i < values.length; i += 3) {
                        if (i + 1 < values.length) {
                            String amountString = values[i + 1].trim();
                            try {
                                balance += Double.parseDouble(amountString); // Update balance with existing transaction amounts
                            } catch (NumberFormatException e) {
                                System.err.println("Failed to parse existing amount: " + amountString);
                            }
                        }
                    }

                    // Update the balance based on the operation
                    if (operation.equals("transfer")) {
                        balance += amount; // Add the amount for transfer
                    } else if (operation.equals("withdrawal")) {
                        balance -= amount; // Subtract the amount for withdrawal
                    } else if (operation.equals("loan")) {
                        balance += amount; // Add the amount for loan
                    } else if (operation.equals("deposit")) {
                        balance += amount; // Add the amount for deposit
                    }

                    // Write the updated line with the new transaction
                    String newTransaction = String.format("%s,%s,%s", currentDate, amount, operation.substring(0, 1).toUpperCase() + operation.substring(1).toLowerCase());
                    StringBuilder updatedLine = new StringBuilder(line);
                    updatedLine.append(",").append(newTransaction); // Append the new transaction to the line
                    bw.write(updatedLine.toString()); // Write the updated line
                    accountUpdated = true;
                } else {
                    bw.write(line); // Write the unchanged line for other accounts
                }
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

    private String loadTransactionHistory(String accountNumber) throws IOException {
        StringBuilder transactionHistory = new StringBuilder();
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                // Check if the row corresponds to the client and matches the account number
                if (values.length > 5 && "Client".equalsIgnoreCase(values[0]) && values[1].trim().equals(accountNumber)) {
                    // Start reading transactions from index 5
                    for (int i = 5; i < values.length; i += 3) {
                        if (i + 2 < values.length) { // Ensure there's enough data for Date, Amount, Description
                            String date = values[i].trim();
                            String amount = values[i + 1].trim();
                            String description = values[i + 2].trim();
                            transactionHistory.append(String.format("Date: %s, Amount: %s, Description: %s%n", date, amount, description));
                        }
                    }
                    return transactionHistory.toString(); // Return the formatted transaction history
                }
            }
        }
        return null; // No matching account found
    }

    public void handleViewClient(ActionEvent actionEvent) {
        String accountNumber = accountNumberTextField.getText().trim();
        if (accountNumber.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Please enter an account number.");
            return;
        }

        try {
            // Load account details
            Account account = loadAccountDetails(accountNumber);
            if (account != null) {
                String details = String.format("Account Number: %s\nAccount Holder: %s\nStatus: %s\nBalance: $%.2f",
                        account.getAccountNumber(), account.getAccountHolderName(), account.getStatus(), account.getBalance());
                outputLabel.setText(details);

                // Load transaction history
                String transactionHistory = loadTransactionHistory(accountNumber);
                if (transactionHistory != null) {
                    transactionHistoryTextArea.setText(transactionHistory); // Display transaction history
                } else {
                    transactionHistoryTextArea.setText("No transaction history found for this account.");
                }
            } else {
                showAlert(AlertType.ERROR, "Error", "Account not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to load account details or transaction history.");
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

        public double getBalance() {
            return balance;
        }
    }
}

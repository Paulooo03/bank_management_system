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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class AdminController {

    @FXML
    private AnchorPane clientPane;

    @FXML
    private TextField accountNumberTextField;

    @FXML
    private Button checkAccountStatusButton;

    @FXML
    private Label accountNumberLabel;

    @FXML
    private Label accountHolderLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label transactionHistoryLabel;

    @FXML
    private Label accountTypeLabel; // Added label for account type

    @FXML
    private TableView<Transaction> transactionsTableView;

    @FXML
    private TableColumn<Transaction, String> dateColumn;

    @FXML
    private TableColumn<Transaction, Double> amountColumn;

    @FXML
    private TableColumn<Transaction, String> descriptionColumn;

    @FXML
    private Button activateOrDeactivateButton;

    @FXML
    private VBox transferMoneyBox;

    @FXML
    private TextField transferAmountTextField;

    @FXML
    private TextField transferOperationTextField;

    @FXML
    private Button transferMoneyButton;

    @FXML
    private Button submitTransferButton;

    @FXML
    private Button cancelTransferButton;

    private final List<Client> clients = new ArrayList<>();
    private int numColumns = 0;
    private Client currentClient;

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

    public static class Client {
        String position;
        String username;
        String status;
        int accountNumber;
        List<Transaction> transactions;

        public Client(String position, String username, String status, int accountNumber, List<Transaction> transactions) {
            this.position = position;
            this.username = username;
            this.status = status;
            this.accountNumber = accountNumber;
            this.transactions = transactions;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        public int getAccountNumber() {
            return accountNumber;
        }

        public double getBalance() {
            double balance = 0;
            for (Transaction transaction : transactions) {
                balance += transaction.getAmount();
            }
            return balance;
        }

        public String getPosition() {
            return position;
        }
    }

    public static class Transaction {
        private final String date;
        private final double amount;
        private final String description;

        public Transaction(String date, double amount, String description) {
            this.date = date;
            this.amount = amount;
            this.description = description;
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
    }

    @FXML
    public void initialize() {
        clearLabels();
        accountNumberTextField.setPromptText("Enter Account Number");
        checkAccountStatusButton.setDisable(true);
        loadClients();

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        accountNumberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkAccountStatusButton.setDisable(newValue.trim().isEmpty());
        });

        transactionsTableView.setVisible(false);
        balanceLabel.setVisible(false);
        transactionHistoryLabel.setVisible(false);
        transferMoneyBox.setVisible(false);
    }

    private void loadClients() {
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (numColumns == 0 && values.length >= 4) {
                    numColumns = values.length;
                }
                if (values.length >= 4 && ("Client".equalsIgnoreCase(values[0]) || "Manager".equalsIgnoreCase(values[0]) || "Teller".equalsIgnoreCase(values[0]))) {
                    int accountNumber = Integer.parseInt(values[1].trim());
                    List<Transaction> transactions = new ArrayList<>();

                    for (int i = 5; i < values.length; i += 3) {
                        if (i + 2 < values.length && !values[i].isEmpty() && !values[i + 1].isEmpty()) {
                            String date = values[i].trim();
                            double amount = Double.parseDouble(values[i + 1].trim());
                            String description = values[i + 2].trim();

                            transactions.add(new Transaction(date, amount, description));
                        }
                    }

                    clients.add(new Client(values[0], values[2], values[4], accountNumber, transactions));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCheckAccountStatusButtonAction() {
        String accountNumber = accountNumberTextField.getText();
        Client client = getClientDetails(accountNumber);
        currentClient = client;
        if (client == null) {
            clearLabels();
            accountNumberLabel.setText("Account not found.");
            return;
        }

        accountNumberLabel.setText(String.valueOf(client.getAccountNumber()));
        accountHolderLabel.setText(client.username);
        statusLabel.setText(client.status);
        balanceLabel.setText(String.format("%.2f", client.getBalance()));
        accountTypeLabel.setText(client.getPosition()); // Set account type label
        updateButtonText(client.status);

        ObservableList<Transaction> transactionsObservableList = FXCollections.observableArrayList(client.getTransactions());
        transactionsTableView.setItems(transactionsObservableList);

        transactionsTableView.setVisible(true);
        balanceLabel.setVisible(true);
        transactionHistoryLabel.setVisible(true);
    }

    private void updateButtonText(String status) {
        if ("Active".equalsIgnoreCase(status)) {
            activateOrDeactivateButton.setText("Deactivate");
        } else {
            activateOrDeactivateButton.setText("Activate");
        }
    }

    @FXML
    public void handleActivateOrDeactivateButtonAction(ActionEvent actionEvent) {
        if (currentClient != null) {
            if ("Active".equalsIgnoreCase(currentClient.status)) {
                currentClient.status = "Inactive";
            } else {
                currentClient.status = "Active";
            }
            updateButtonText(currentClient.status);
            updateCSVStatus();
            statusLabel.setText(currentClient.status);
        }
    }

    private void updateCSVStatus() {
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",\\s*");

                if (values.length >= 5 && ("Client".equalsIgnoreCase(values[0]) || "Manager".equalsIgnoreCase(values[0]) || "Teller".equalsIgnoreCase(values[0]))) {
                    try {
                        int parsedAccountNumber = Integer.parseInt(values[1].trim());
                        if (parsedAccountNumber == currentClient.accountNumber) {
                            values[4] = currentClient.status;
                            line = String.join(",", values);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping line due to number format issue: " + line);
                    }
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String updatedLine : lines) {
                bw.write(updatedLine);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTransferMoney(ActionEvent event) {
        transferMoneyBox.setVisible(true);
    }

    @FXML
    private void handleSubmitTransfer(ActionEvent event) {
        String accountNumber = accountNumberTextField.getText().trim();
        String amountString = transferAmountTextField.getText().trim();
        String operation = transferOperationTextField.getText().trim().toLowerCase();

        if (accountNumber.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter an account number.");
            return;
        }
        if (amountString.isEmpty() || operation.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter both amount and operation.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountString);

            // Validate the operation and amount based on the operation type
            if (operation.equals("transfer") || operation.equals("loan") || operation.equals("deposit")) {
                if (amount <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Amount must be positive for Transfer, Loan, or Deposit.");
                    return;
                }
            } else if (operation.equals("withdraw")) {
                if (amount <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Amount must be positive for Withdraw.");
                    return;
                }

                // Check if the client has sufficient funds for withdrawal
                double currentBalance = currentClient.getBalance();
                if (amount > currentBalance) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Insufficient funds for withdrawal.");
                    return;
                }

                // Deduct the amount for withdrawal
                amount = -amount;
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid operation. Please enter 'transfer', 'loan', 'deposit', or 'withdraw'.");
                return;
            }

            // Create and add the new transaction
            String date = java.time.LocalDate.now().toString();
            String description = operation.substring(0, 1).toUpperCase() + operation.substring(1);

            Transaction transaction = new Transaction(date, amount, description);
            currentClient.getTransactions().add(transaction);

            // Update the transactions table
            transactionsTableView.getItems().add(transaction);

            // Update the balance label
            balanceLabel.setText(String.format("%.2f", currentClient.getBalance()));

            // Save the updated client data back to the CSV file
            saveClientTransactions(currentClient);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction completed successfully.");
            transferMoneyBox.setVisible(false);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid amount entered. Please enter a valid number.");
        }
    }

    @FXML
    private void handleCancelTransfer(ActionEvent event) {
        transferMoneyBox.setVisible(false);
    }

    private void saveClientTransactions(Client client) {
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",\\s*");

                if (values.length >= 5 && ("Client".equalsIgnoreCase(values[0]) || "Manager".equalsIgnoreCase(values[0]) || "Teller".equalsIgnoreCase(values[0]))) {
                    try {
                        int parsedAccountNumber = Integer.parseInt(values[1].trim());
                        if (parsedAccountNumber == currentClient.accountNumber) {
                            // Update the transactions for the current client
                            StringBuilder updatedLine = new StringBuilder(String.join(",", values[0], values[1], values[2], values[3], values[4]));

                            for (Transaction transaction : client.getTransactions()) {
                                updatedLine.append(",").append(transaction.getDate())
                                        .append(",").append(transaction.getAmount())
                                        .append(",").append(transaction.getDescription());
                            }
                            line = updatedLine.toString();
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping line due to number format issue: " + line);
                    }
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String updatedLine : lines) {
                bw.write(updatedLine);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Client getClientDetails(String accountNumber) {
        try {
            int parsedAccountNumber = Integer.parseInt(accountNumber);
            for (Client client : clients) {
                if (client.getAccountNumber() == parsedAccountNumber) {
                    return client;
                }
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    private void clearLabels() {
        accountNumberLabel.setText("");
        accountHolderLabel.setText("");
        balanceLabel.setText("");
        statusLabel.setText("");
        transactionHistoryLabel.setText("");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void switchToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

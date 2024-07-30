package com.example.bank_management_system;

import com.example.bank_management_system.TransactionHistoryController.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
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
    private TableView<Transaction> transactionsTableView;

    @FXML
    private TableColumn<Transaction, String> dateColumn;

    @FXML
    private TableColumn<Transaction, Double> amountColumn;

    @FXML
    private TableColumn<Transaction, String> descriptionColumn;

    @FXML
    private Button activateOrDeactivateButton;

    private final List<Client> clients = new ArrayList<>();
    private int numColumns = 0;
    private Client currentClient;

    public static class Client {
        String username;
        String status;
        int accountNumber;
        List<Transaction> transactions;

        public Client(String username, String status, int accountNumber, List<Transaction> transactions) {
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
                balance += transaction.getAmount(); // Sum amounts for balance
            }
            return balance;
        }
    }

    @FXML
    public void initialize() {
        clearLabels();
        accountNumberTextField.setPromptText("Enter Account Number");
        checkAccountStatusButton.setDisable(true);
        loadClients();

        // Set up the TableView columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("type")); // Updated to match CSV

        accountNumberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkAccountStatusButton.setDisable(newValue.trim().isEmpty());
        });
    }

    private void loadClients() {
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (numColumns == 0 && values.length >= 4 && "Client".equalsIgnoreCase(values[0])) {
                    numColumns = values.length;
                }
                if (values.length >= 4 && "Client".equalsIgnoreCase(values[0])) {
                    int accountNumber = Integer.parseInt(values[1].trim());
                    List<Transaction> transactions = new ArrayList<>();

                    // Parsing transactions
                    for (int i = 5; i < values.length; i += 3) { // Updated to read date, amount, and type
                        if (i + 2 < values.length && !values[i].isEmpty() && !values[i + 1].isEmpty()) {
                            String date = values[i].trim();
                            double amount = Double.parseDouble(values[i + 1].trim());
                            String type = values[i + 2].trim();

                            transactions.add(new Transaction(date, amount, type)); // Adjust transaction type accordingly
                        }
                    }

                    clients.add(new Client(values[2], values[4], accountNumber, transactions));
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
        currentClient = client; // Store the current client
        if (client == null) {
            clearLabels();
            accountNumberLabel.setText("Account not found.");
            return;
        }

        accountNumberLabel.setText(String.valueOf(client.getAccountNumber()));
        accountHolderLabel.setText(client.username);
        statusLabel.setText(client.status);
        balanceLabel.setText(String.format("%.2f", client.getBalance()));
        updateButtonText(client.status);

        // Load transactions and display in the TableView
        ObservableList<Transaction> transactionsObservableList = FXCollections.observableArrayList(client.getTransactions());
        transactionsTableView.setItems(transactionsObservableList);
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

                if (values.length >= 5 && "Client".equalsIgnoreCase(values[0])) {
                    try {
                        int parsedAccountNumber = Integer.parseInt(values[1].trim());
                        if (parsedAccountNumber == currentClient.accountNumber) {
                            values[4] = currentClient.status; // Update status in CSV
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

    private void clearLabels() {
        accountNumberLabel.setText("");
        accountHolderLabel.setText("");
        balanceLabel.setText("");
        statusLabel.setText("");
    }

    private Client getClientDetails(String accountNumber) {
        for (Client client : clients) {
            if (String.valueOf(client.getAccountNumber()).equals(accountNumber)) {
                return client;
            }
        }
        return null;
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

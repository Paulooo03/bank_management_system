package com.example.bank_management_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

public class UserTransactionHistoryController {

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

                    // Start parsing transactions from the 6th index (after Status, Date, Amount)
                    for (int i = 6; i < values.length; i += 3) { // Increment by 3 for Date, Amount, Description
                        if (i + 2 < values.length) { // Ensure there's a date, amount, and description
                            String date = values[i].trim(); // Date
                            double amount = Double.parseDouble(values[i + 1].trim()); // Amount
                            String description = values[i + 2].trim(); // Description
                            latestBalance += amount; // Update the running balance
                            transactions.add(new Transaction(date, amount, description, latestBalance));
                        }
                    }
                    break; // Stop after processing the correct account
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error parsing a number: " + e.getMessage());
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

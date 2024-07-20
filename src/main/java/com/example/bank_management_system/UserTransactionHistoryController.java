package com.example.bank_management_system;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class UserTransactionHistoryController {

    @FXML
    private TableView<Transaction> transactionTable;

    @FXML
    private TableColumn<Transaction, String> dateColumn;

    @FXML
    private TableColumn<Transaction, Double> amountColumn;

    @FXML
    private TableColumn<Transaction, Double> balanceColumn;

    @FXML
    public void initialize() {
        // Initialize table columns with cell value factories
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        balanceColumn.setCellValueFactory(cellData -> cellData.getValue().balanceProperty().asObject());
    }

    public void setTransactions(ObservableList<Transaction> transactions) {
        transactionTable.setItems(transactions);
    }

    public ObservableList<Transaction> loadTransactions(String accountNumber) {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
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
                        transactions.add(new Transaction(date, amount, balance));
                    }
                    break; // Assuming account numbers are unique, stop after finding the correct account
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @FXML
    public void handleBackButton() {
        // Implement back button logic
    }

    @FXML
    public void handleExportCSV() {
        // Implement CSV export logic
    }

    public static class Transaction {
        private final StringProperty date;
        private final DoubleProperty amount;
        private final DoubleProperty balance;

        public Transaction(String date, double amount, double balance) {
            this.date = new SimpleStringProperty(date);
            this.amount = new SimpleDoubleProperty(amount);
            this.balance = new SimpleDoubleProperty(balance);
        }

        public StringProperty dateProperty() {
            return date;
        }

        public DoubleProperty amountProperty() {
            return amount;
        }

        public DoubleProperty balanceProperty() {
            return balance;
        }

        public double getBalance() {
            return balance.get();
        }
    }
}

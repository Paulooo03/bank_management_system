package com.example.bank_management_system;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

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
    private TableColumn<Transaction, Double> balanceColumn;

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        balanceColumn.setCellValueFactory(cellData -> cellData.getValue().balanceProperty().asObject());
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
                if (values.length >= 5 && "Client".equalsIgnoreCase(values[0]) && values[1].trim().equals(accountNumber)) {
                    clientName = values[2].trim(); // Assume client name is in the third column
                    for (int i = 5; i < values.length; i += 2) {
                        if (i + 1 < values.length) { // Ensure there's both a date and amount
                            String date = values[i].trim();
                            double amount = Double.parseDouble(values[i + 1].trim());
                            latestBalance += amount; // Update the balance incrementally
                            transactions.add(new Transaction(date, amount, latestBalance));
                        }
                    }
                    break; // Assuming account numbers are unique, stop after finding the correct account
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the transactions
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
                writer.write("Date,Amount,Balance\n");
                for (Transaction transaction : transactions) {
                    writer.write(String.format("%s,%.2f,%.2f\n",
                            transaction.getDate(), transaction.getAmount(), transaction.getBalance()));
                }
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

        public String getDate() {
            return date.get();
        }

        public double getAmount() {
            return amount.get();
        }

        public double getBalance() {
            return balance.get();
        }
    }
}

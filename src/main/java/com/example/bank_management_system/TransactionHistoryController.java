package com.example.bank_management_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TransactionHistoryController {
    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<Transaction, String> dateColumn;
    @FXML
    private TableColumn<Transaction, String> typeColumn;
    @FXML
    private TableColumn<Transaction, Double> amountColumn;
    @FXML
    private TableColumn<Transaction, Double> balanceColumn;

    private ObservableList<Transaction> transactions;
    private int accountNumber;

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
    }

    public void setTransactions(ObservableList<Transaction> transactions) {
        this.transactions = transactions;
        transactionTable.setItems(transactions);
    }

    @FXML
    private void handleExportCSV(ActionEvent event) {
        if (transactions == null || transactions.isEmpty()) {
            // No transactions to export
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transaction History");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Write CSV header
                writer.write("ClientUsername,Date, Amount,Balance\n");

                // Write each transaction to CSV
                for (Transaction transaction : transactions) {
                    writer.write(String.format("%s,%s,%.2f,%.2f\n",
                            "ClientUsername", transaction.getDate(),
                            transaction.getAmount(), transaction.getBalance()));
                }

                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
                // Handle file writing error
            }
        }
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void handleBackButton(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public record Transaction(String date, double amount, double balance) {
        public String getDate() {
            return date;
        }

        public double getAmount() {
            return amount;
        }

        public double getBalance() {
            return balance;
        }
    }
}

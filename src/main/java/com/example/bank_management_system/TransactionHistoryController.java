package com.example.bank_management_system;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
    @FXML
    private TableColumn<Transaction, String> descriptionColumn;
    @FXML
    private Button activateOrDeactivate;

    private ObservableList<Transaction> transactions;
    private int accountNumber;
    private String status;
    private String username;

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description")); // Ensure this is set
    }

    public void setTransactions(ObservableList<Transaction> transactions) {
        this.transactions = transactions;
        transactionTable.setItems(transactions);
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setStatus(String status) {
        this.status = status;
        updateButtonText();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private void updateButtonText() {
        if ("Active".equalsIgnoreCase(status)) {
            activateOrDeactivate.setText("Deactivate");
        } else {
            activateOrDeactivate.setText("Activate");
        }
    }

    @FXML
    private void handleExportCSV(ActionEvent event) {
        if (transactions == null || transactions.isEmpty()) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transaction History");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("ClientUsername,Date,Type,Amount,Balance\n");
                double runningBalance = 0; // Initialize running balance
                for (Transaction transaction : transactions) {
                    runningBalance += transaction.getAmount(); // Update running balance
                    writer.write(String.format("%s,%s,%s,%.2f,%.2f\n",
                            username, transaction.getDate(), transaction.getType(),
                            transaction.getAmount(), runningBalance)); // Write running balance
                }
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleBackButton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin_view.fxml"));
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
    public void onActivateOrDeactivateClick(ActionEvent actionEvent) {
        if ("Active".equalsIgnoreCase(status)) {
            status = "Inactive";
        } else {
            status = "Active";
        }
        updateButtonText();
        updateCSVStatus();
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
                        if (parsedAccountNumber == accountNumber) {
                            values[4] = status; // Update status in CSV
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

        public String getType() {
            return type;
        }

        public double getAmount() {
            return amount;
        }

        public double getBalance() {
            return amount; // Adjust this if you want a specific balance calculation
        }
    }
}

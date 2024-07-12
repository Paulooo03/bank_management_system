package com.example.bank_management_system;

import javafx.collections.FXCollections;
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
    private Button activateOrDeactivate;

    private ObservableList<Transaction> transactions;
    private int accountNumber;
    private String status;
    private String username;

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
                writer.write("ClientUsername,Date,Amount,Balance\n");
                for (Transaction transaction : transactions) {
                    writer.write(String.format("%s,%s,%.2f,%.2f\n",
                            username, transaction.getDate(),
                            transaction.getAmount(), transaction.getBalance()));
                }
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleBackButton(ActionEvent actionEvent) {
        try {
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

    public void onactivateOrDeactivateClick(ActionEvent actionEvent) {
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
                // Debug statement to check each line being read
                System.out.println("Reading line: " + line);

                // Splitting on comma followed by zero or more spaces
                String[] values = line.split(",\\s*");

                if (values.length >= 5 && "Client".equalsIgnoreCase(values[0])) {
                    try {
                        // Remove any leading or trailing whitespace from account number
                        int parsedAccountNumber = Integer.parseInt(values[1].trim());
                        if (parsedAccountNumber == accountNumber) {
                            System.out.println("Updating status for account number: " + accountNumber); // Debug statement
                            values[4] = status; // Update the status
                            line = String.join(",", values);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping line due to number format issue: " + line); // Debug statement
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

        System.out.println("CSV file updated."); // Debug statement
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

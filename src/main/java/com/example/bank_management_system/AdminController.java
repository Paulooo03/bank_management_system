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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminController {

    @FXML
    private AnchorPane clientPane;

    private final List<Client> clients = new ArrayList<>();
    private int numColumns = 0;

    public static class Client {
        String username;
        String status;
        int accountNumber;
        List<Transaction> transactions; // List of transactions

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
    }

    @FXML
    public void initialize() {
        loadClients();
        loadTransactions();

        // Debug: Print number of clients read
        System.out.println("Number of clients read: " + clients.size());
        System.out.println("Number of columns per client: " + numColumns);

        // Dynamically create UI elements based on the number of clients
        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            Label nameLabel = new Label(client.username);
            nameLabel.setLayoutX(31.0);
            nameLabel.setLayoutY(55.0 + i * 33.0);
            Label statusLabel = new Label(client.status);
            statusLabel.setLayoutX(235.0);
            statusLabel.setLayoutY(55.0 + i * 33.0);
            Button viewButton = new Button("view");
            viewButton.setLayoutX(354.0);
            viewButton.setLayoutY(51.0 + i * 33.0);
            final int clientIndex = i;
            viewButton.setOnAction(event -> onViewClick(clientIndex));

            clientPane.getChildren().addAll(nameLabel, statusLabel, viewButton);
        }

        // Adjust AnchorPane height based on the number of clients
        double newHeight = 55.0 + clients.size() * 33.0 + 20.0; // Adding extra space
        clientPane.setPrefHeight(newHeight);
    }

    private void loadClients() {
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("Reading line: " + line); // Debug statement
                String[] values = line.split(",");
                // Determine the number of columns in the first data line
                if (numColumns == 0 && values.length >= 4 && "Client".equalsIgnoreCase(values[0])) {
                    numColumns = values.length;
                }
                if (values.length >= 4 && "Client".equalsIgnoreCase(values[0])) {
                    int accountNumber = Integer.parseInt(values[1].trim());
                    clients.add(new Client(values[2], values[4], accountNumber, new ArrayList<>()));
                    System.out.println("Added client: " + values[2] + ", status: " + values[4]); // Debug statement
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTransactions() {
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 6 && "Client".equalsIgnoreCase(values[0])) {
                    int accountNumber = Integer.parseInt(values[1].trim());

                    // Extract transaction values from CSV line, assuming dates are in the CSV
                    List<Transaction> transactionObjects = new ArrayList<>();
                    for (int i = 5; i < values.length; i++) {
                        String[] transactionParts = values[i].split(";");
                        if (transactionParts.length == 3) {
                            String date = transactionParts[0].trim();
                            double amount = Double.parseDouble(transactionParts[1].trim());
                            String description = transactionParts[2].trim();
                            transactionObjects.add(new Transaction(date, amount, description));
                        }
                    }

                    // Find the client by account number and add transactions
                    for (Client client : clients) {
                        if (client.getAccountNumber() == accountNumber) {
                            client.transactions.addAll(transactionObjects);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("user_selection.fxml"));
            Scene scene = new Scene(root, 1280, 720);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Le Bank Management System");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onViewClick(int clientIndex) {
        Client selectedClient = clients.get(clientIndex);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("transaction_history.fxml"));
            Parent root = loader.load();

            TransactionHistoryController controller = loader.getController();
            ObservableList<Transaction> transactionsObservableList = FXCollections.observableArrayList(selectedClient.getTransactions());
            controller.setTransactions(transactionsObservableList);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Transaction History for " + selectedClient.username);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
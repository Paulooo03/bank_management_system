package com.example.bank_management_system;

import com.example.bank_management_system.TransactionHistoryController.Transaction; // Ensure correct import

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
import java.util.Objects;

public class HelloController {
    public Button onAdminClick;
    public Button onClientClick;
    public Button onTellerClick;
    public Button onManagerClick;
    @FXML
    private AnchorPane clientPane;

    private final List<Client> clients = new ArrayList<>();
    private int numColumns = 0;

    @FXML
    private void onAdminClick(ActionEvent actionEvent) {
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

    public void onClientClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onTellerClick(ActionEvent actionEvent) {
        // Implement teller click logic
    }

    public void onManagerClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("manager.fxml"));
            Parent managerView = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(managerView);
            stage.setScene(scene);
            stage.setTitle("Manager Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //dont touch this admin part
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
        /*
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
            viewButton.setOnAction(event -> onViewClick(event, clientIndex));

            clientPane.getChildren().addAll(nameLabel, statusLabel, viewButton);
        }

        // Adjust AnchorPane height based on the number of clients
        double newHeight = 55.0 + clients.size() * 33.0 + 20.0; // Adding extra space
        clientPane.setPrefHeight(newHeight);

         */
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
                    for (int i = 5; i < values.length; i += 2) {
                        String date = values[i].trim();
                        double amount = Double.parseDouble(values[i + 1].trim());
                        // Balance calculation assuming it's cumulative
                        double balance = transactionObjects.isEmpty() ? amount : transactionObjects.get(transactionObjects.size() - 1).getBalance() + amount;
                        transactionObjects.add(new Transaction(date, amount, balance));
                    }

                    // Find the client in the list and add transactions
                    for (Client client : clients) {
                        if (client.getAccountNumber() == accountNumber) {
                            client.transactions.addAll(transactionObjects);
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onViewClick(ActionEvent actionEvent, int clientIndex) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("transaction_history.fxml"));
            Parent root = loader.load();

            TransactionHistoryController controller = loader.getController();
            ObservableList<TransactionHistoryController.Transaction> transactions = FXCollections.observableArrayList(clients.get(clientIndex).getTransactions());
            controller.setTransactions(transactions);

            int accountNumber = clients.get(clientIndex).getAccountNumber();
            controller.setAccountNumber(accountNumber);

            String status = clients.get(clientIndex).status;
            controller.setStatus(status);

            String username = clients.get(clientIndex).username;
            controller.setUsername(username);

            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //until here is admin part
}

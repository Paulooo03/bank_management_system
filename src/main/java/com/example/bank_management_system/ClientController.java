package com.example.bank_management_system;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientController {

    @FXML
    private TextField accountNumberInput;

    @FXML
    private TextField passwordInput;

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
/*
    @FXML
    public void onClickLogin(ActionEvent actionEvent) {
        String accountNumber = accountNumberInput.getText().trim();
        String password = passwordInput.getText().trim();

        // Uncomment the next lines if you want to use validation
        // if (validateUser(accountNumber, password)) {
        showUserTransactionHistory(accountNumber);
        // } else {
        //     // Handle invalid login
        //     System.out.println("Invalid login credentials.");
        // }
    }*/

    private boolean validateUser(String accountNumber, String password) {
        // Implement validation logic here. For example, check against a list of users or a database.
        // This is a placeholder implementation:
        return accountNumber.equals("12345") && password.equals("password"); // Replace with actual validation
    }
    /*
    private void showUserTransactionHistory(String accountNumber) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user_transaction_history.fxml"));
            Parent root = loader.load();

            UserTransactionHistoryController controller = loader.getController();
            ObservableList<UserTransactionHistoryController.Transaction> transactions = controller.loadTransactions(accountNumber);
            controller.setTransactions(transactions);

            Scene scene = new Scene(root);
            Stage currentStage = (Stage) accountNumberInput.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}

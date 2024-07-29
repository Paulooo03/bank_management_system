package com.example.bank_management_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HelloController {

    @FXML
    private Button onAdminClick;
    @FXML
    private Button onClientClick;
    @FXML
    private Button onTellerClick;
    @FXML
    private Button onManagerClick;

    private final Map<String, String> managerCredentials = new HashMap<>() {{
        put("Mike Ehrmantraut", "Pimento cheese");
    }};

    private final Map<String, String> tellerCredentials = new HashMap<>() {{
        put("Gus Fring", "Mang Inasal");
    }};

    private final Map<String, String> clientCredentials = new HashMap<>() {{
        put("Walter White", "Baby Blue");
        put("Sibal", "Minecraft");
        put("Homelander", "Milk");
        put("Elon Musk", "Tesla");
        put("Ella", "Genshin");
        put("Lumine", "Diluc");
    }};

    @FXML
    private void onAdminClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin_view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 720); // Set scene size
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClientClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1280, 720); // Set scene size
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onTellerClick(ActionEvent actionEvent) {
        if (showLoginDialog("Teller")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("teller.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 1280, 720); // Set scene size
                Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                currentStage.setScene(scene);
                currentStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onManagerClick(ActionEvent actionEvent) {
        if (showLoginDialog("Manager")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("manager.fxml"));
                Parent managerView = loader.load();
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(managerView, 1280, 720); // Set scene size
                stage.setScene(scene);
                stage.setTitle("Manager Dashboard");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean showLoginDialog(String role) {
        javafx.scene.control.Dialog<Boolean> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Account Login");

        // Set the dialog content
        TextField usernameField = new TextField();
        TextField passwordField = new TextField();
        usernameField.setPromptText("Account Name");
        passwordField.setPromptText("Password");

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.add(new javafx.scene.control.Label("Account Name:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new javafx.scene.control.Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Add buttons for login
        javafx.scene.control.ButtonType loginButtonType = new javafx.scene.control.ButtonType("Login", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, javafx.scene.control.ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                String username = usernameField.getText().trim();
                String password = passwordField.getText().trim();
                boolean valid = validateLogin(role, username, password);
                if (!valid) {
                    showAlert(AlertType.ERROR, "Login Failed", "Invalid username or password. Please try again.");
                    return false;
                }
                return valid;
            }
            return null; // Return null when Cancel is clicked or dialog is closed
        });

        Boolean result = dialog.showAndWait().orElse(null);
        return result != null && result;
    }

    private boolean validateLogin(String role, String username, String password) {
        Map<String, String> credentials;
        switch (role) {
            case "Manager":
                credentials = managerCredentials;
                break;
            case "Teller":
                credentials = tellerCredentials;
                break;
            case "Client":
                return true; // Bypass login for Client
            default:
                return false;
        }
        return password.equals(credentials.get(username));
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
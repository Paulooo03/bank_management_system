package com.example.bank_management_system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            System.out.println("Loading user_selection.fxml...");
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user_selection.fxml")));
            Scene scene = new Scene(root, 1280, 720);
            stage.setScene(scene);
            stage.setTitle("Le Bank Management System");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

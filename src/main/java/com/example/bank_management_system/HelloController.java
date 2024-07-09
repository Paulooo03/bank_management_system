package com.example.bank_management_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void onClient_1_viewClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(getClass().getResource("client_1.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onclient_2_viewClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(getClass().getResource("client_2.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void onclient_3_viewClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(getClass().getResource("client_3.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onclient_4_viewClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(getClass().getResource("client_4.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onclient_1_backClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onclient_2_backClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onclient_3_backClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onclient_4_backClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onactive_client_1Click(ActionEvent actionEvent) {
    }

    public void onactive_client_2Click(ActionEvent actionEvent) {
    }

    public void onactive_client_3Click(ActionEvent actionEvent) {
    }

    public void onactive_client_4Click(ActionEvent actionEvent) {
    }
}
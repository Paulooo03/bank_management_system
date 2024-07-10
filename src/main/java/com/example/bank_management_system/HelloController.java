package com.example.bank_management_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HelloController {
    @FXML
    private Label client_1, client_2, client_3, client_4, status_client_1, status_client_2, status_client_3, status_client_4;

    private Stage stage;
    private Scene scene;
    private Parent root;

    ArrayList<String> clientUsernames = new ArrayList<>();
    ArrayList<String> clientStatuses = new ArrayList<>();

    @FXML
    public void initialize() {
        String filePath = "D:/School files/1st sem/3rd term/LBYCPEI/bank_management_system/bank_management_system-main/src/main/resources/bank_database.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4 && "Client".equalsIgnoreCase(values[0])) {
                    clientUsernames.add(values[1]); // Add username to ArrayList
                    clientStatuses.add(values[3]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Client Usernames: " + clientUsernames);
        System.out.println("Client Statuses: " + clientStatuses);

        if (!clientUsernames.isEmpty()){ client_1.setText(clientUsernames.get(0));}
        if (clientUsernames.size() > 1) {client_2.setText(clientUsernames.get(1));}
        if (clientUsernames.size() > 2) {client_3.setText(clientUsernames.get(2));}
        if (clientUsernames.size() > 3) {client_4.setText(clientUsernames.get(3));}

        if (!clientStatuses.isEmpty()) {
            status_client_1.setText(clientStatuses.get(0));
        }
        if (clientStatuses.size() > 1) {
            status_client_2.setText(clientStatuses.get(1));
        }
        if (clientStatuses.size() > 2) {
            status_client_3.setText(clientStatuses.get(2));
        }
        if (clientStatuses.size() > 3) {
            status_client_4.setText(clientStatuses.get(3));
        }
    }



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
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onclient_3_backClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
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
        // Implement the desired action for active client 1 button click
    }

    public void onactive_client_2Click(ActionEvent actionEvent) {
        // Implement the desired action for active client 2 button click
    }

    public void onactive_client_3Click(ActionEvent actionEvent) {
        // Implement the desired action for active client 3 button click
    }

    public void onactive_client_4Click(ActionEvent actionEvent) {
        // Implement the desired action for active client 4 button click
    }
}

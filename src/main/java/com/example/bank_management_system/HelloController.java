package com.example.bank_management_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class HelloController {
    @FXML
    public Button client_4_view;
    @FXML
    public Button client_3_view;
    @FXML
    public Button client_2_view;
    @FXML
    public Button client_1_view;
    public Label client_1_name;
    public Button client_1_back;
    public Label client_1_history_1;
    public Label client_1_history_2;
    public Label client_1_history_3;
    public Label client_1_history_4;
    public Label client_2_name;
    public Button client_2_back;
    public Label client_2_history_1;
    public Label client_2_history_2;
    public Label client_2_history_3;
    public Label client_2_history_4;
    public Button active_client_2;
    public Button active_client_1;
    public Label client_3_name;
    public Button client_3_back;
    public Label client_3_history_1;
    public Label client_3_history_2;
    public Label client_3_history_3;
    public Label client_3_history_4;
    public Button active_client_3;
    public Label client_4_name;
    public Button client_4_back;
    public Label client_4_history_1;
    public Label client_4_history_2;
    public Label client_4_history_3;
    public Label client_4_history_4;
    public Button active_client_4;
    @FXML
    private Label client_1;
    @FXML
    private Label client_2;
    @FXML
    private Label client_3;
    @FXML
    private Label client_4;
    @FXML
    private Label status_client_1;
    @FXML
    private Label status_client_2;
    @FXML
    private Label status_client_3;
    @FXML
    private Label status_client_4;

    private Stage stage;
    private Scene scene;
    private Parent root;

    String[] clientUsernames = new String[4];
    String[] clientStatuses = new String[4];

    @FXML
    public void initialize() {
        String filePath = "src/main/resources/bank_database.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int index = 0;
            while ((line = br.readLine()) != null && index < 4) {
                System.out.println("Reading line: " + line); // Debug statement
                String[] values = line.split(",");
                if (values.length >= 4 && "Client".equalsIgnoreCase(values[0])) {
                    clientUsernames[index] = values[1]; // Add username to array
                    clientStatuses[index] = values[3]; // Add status to array
                    System.out.println("Added username: " + values[1] + ", status: " + values[3]); // Debug statement
                    index++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Client Usernames: " + String.join(", ", clientUsernames));
        System.out.println("Client Statuses: " + String.join(", ", clientStatuses));

        if (clientUsernames[0] == null) {
            client_1.setText(clientUsernames[0]);
        }
        if (clientUsernames[1] == null) {
            client_2.setText(clientUsernames[1]);
        }
        if (clientUsernames[2] ==null) {
            client_3.setText(clientUsernames[2]);
        }
        if (clientUsernames[3] == null) {
            client_4.setText(clientUsernames[3]);
        }

        if (clientStatuses[0] == null) {
            status_client_1.setText(clientStatuses[0]);
        }
        if (clientStatuses[1] == null) {
            status_client_2.setText(clientStatuses[1]);
        }
        if (clientStatuses[2] == null) {
            status_client_3.setText(clientStatuses[2]);
        }
        if (clientStatuses[3] == null) {
            status_client_4.setText(clientStatuses[3]);
        }
    }

    public void onClient_1_viewClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("client_1.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onclient_2_viewClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("client_2.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onclient_3_viewClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("client_3.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onclient_4_viewClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("client_4.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onclient_1_backClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onclient_2_backClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onclient_3_backClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onclient_4_backClick(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
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

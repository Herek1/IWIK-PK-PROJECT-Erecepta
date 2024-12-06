package com.example.clientservererecepta.Client;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StageHandler {
    private final Stage stage;
    private final ClientHandler clientHandler; // Handles communication
    private TextArea messagesArea;
    private TextField login;
    private TextField password;

    public StageHandler(Stage stage, ClientHandler clientHandler) {
        this.stage = stage;
        this.clientHandler = clientHandler;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public void setDefaultView() {
        VBox defaultLayout = generateDefaultLayout();
        stage.setScene(new Scene(defaultLayout, 400, 300));
        stage.setTitle("E-Prescription System");
        stage.show();
    }

    public void switchToRoleView(User user) {
        if (user == null) {
            displayMessage("Error: User is null.");
            return;
        }
        VBox layout = user.generateLayout();
        stage.setScene(new Scene(layout, 400, 300));
    }



    private VBox generateDefaultLayout() {
        login = new TextField();
        login.setPromptText("Login");

        password = new TextField();
        password.setPromptText("Password");

        messagesArea = new TextArea();
        messagesArea.setEditable(false);

        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> sendLoginData());

        return new VBox(10, messagesArea, login, password, loginButton);
    }

    private void sendLoginData() {
        String loginData = "login;"+login.getText() + ";" + password.getText();
        if (!loginData.isBlank()) {
            clientHandler.sendMessage(loginData);
        } else {
            messagesArea.appendText("Please enter login credentials.\n");
        }
    }
    public void displayMessage(String message) {
        if (messagesArea != null) {
            messagesArea.appendText(message + "\n");
        }
    }
}

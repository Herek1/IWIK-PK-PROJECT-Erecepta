package com.example.clientservererecepta.Client;

import com.example.clientservererecepta.Client.Util.ShowAlert;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StageHandler {
    private final Stage stage;
    private final ClientHandler clientHandler;
    private final TextArea messagesArea;

    public StageHandler(Stage stage, ClientHandler clientHandler) {
        this.stage = stage;
        this.clientHandler = clientHandler;
        this.messagesArea = new TextArea();
        this.messagesArea.setEditable(false);
        this.messagesArea.setPrefHeight(200);
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public TextArea getMessagesArea() {
        return messagesArea;
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
        stage.setScene(new Scene(layout, 600, 400)); // Wider scene for better layout
    }

    public void setScene(Scene scene) {
        Platform.runLater(() -> {
            stage.setScene(scene);
            stage.show();
        });
    }

    private VBox generateDefaultLayout() {
        displayMessage("");
        Label titleLabel = new Label("Login to E-Prescription System");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField loginField = new TextField();
        loginField.setPromptText("Login");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> {
            if (isNumeric(loginField.getText()) && !loginField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
                sendLoginData(loginField, passwordField);
            } else {
                ShowAlert.error("Please enter correct data");
            }
        });

        VBox layout = new VBox(10, titleLabel, loginField, passwordField, loginButton, messagesArea);
        layout.setPadding(new Insets(15));
        layout.setSpacing(10);

        return layout;
    }

    private void sendLoginData(TextField loginField, TextField passwordField) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonResponseNode = objectMapper.createObjectNode();
        jsonResponseNode.put("type", "login");
        jsonResponseNode.put("login", loginField.getText());
        jsonResponseNode.put("password", passwordField.getText());

        String loginData = jsonResponseNode.toString();
        if (!loginData.isBlank()) {
            clientHandler.sendMessage(loginData);
        } else {
            displayMessage("Please enter login credentials.");
        }
    }

    public void displayMessage(String message) {
        Platform.runLater(() -> {
            messagesArea.clear();
            messagesArea.appendText(message + "\n");
            messagesArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

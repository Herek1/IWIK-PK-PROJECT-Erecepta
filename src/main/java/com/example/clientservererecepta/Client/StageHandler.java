package com.example.clientservererecepta.Client;

import com.example.clientservererecepta.Client.Util.ShowAlert;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StageHandler {
    private final Stage stage;
    private final ClientHandler clientHandler;
    private TextArea messagesArea;

    public StageHandler(Stage stage, ClientHandler clientHandler) {
        this.stage = stage;
        this.clientHandler = clientHandler;
        this.messagesArea = new TextArea();
        this.messagesArea.setEditable(false);
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
        stage.setScene(new Scene(layout, 400, 300));
    }

    public void setScene(Scene scene) {
        Platform.runLater(() -> {
            stage.setScene(scene);
            stage.show();
        });
    }

    private VBox generateDefaultLayout() {
        TextField login = new TextField();
        login.setPromptText("Login");

        TextField password = new TextField();
        password.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> {
            if (isNumeric(login.getText()) && !login.getText().isEmpty() && !password.getText().isEmpty()) {
                sendLoginData(login, password);
            }else{
                ShowAlert.error("Please enter correct data");
            }
        });

        return new VBox(10, messagesArea, login, password, loginButton);
    }

    private void sendLoginData(TextField login, TextField password) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonResponseNode = objectMapper.createObjectNode();
        jsonResponseNode.put("type", "login");
        jsonResponseNode.put("login", login.getText());
        jsonResponseNode.put("password",password.getText());

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

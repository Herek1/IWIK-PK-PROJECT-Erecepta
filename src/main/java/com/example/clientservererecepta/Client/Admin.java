package com.example.clientservererecepta.Client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Admin extends User {
    private final ClientHandler clientHandler;
    private final StageHandler stageHandler;

    public Admin(int id, String name, String surname, ClientHandler clientHandler, StageHandler stageHandler) {
        super(id, name, surname);
        this.clientHandler = clientHandler;
        this.stageHandler = stageHandler;
    }

    @Override
    public VBox generateLayout() {
        Label welcomeLabel = new Label("Welcome, " + toString());
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button addUserButton = new Button("Add User");
        addUserButton.setMaxWidth(Double.MAX_VALUE);
        addUserButton.setStyle("-fx-font-size: 14px;");
        addUserButton.setOnAction(event -> openAddUserScene());

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setMaxWidth(Double.MAX_VALUE);
        changePasswordButton.setStyle("-fx-font-size: 14px;");
        changePasswordButton.setOnAction(event -> openChangePasswordScene());

        Button addDrugButton = new Button("Add Drug");
        addDrugButton.setMaxWidth(Double.MAX_VALUE);
        addDrugButton.setStyle("-fx-font-size: 14px;");
        addDrugButton.setOnAction(event -> openAddDrugScene());

        Button logoutButton = new Button("Log Out");
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setStyle("-fx-font-size: 14px;");
        logoutButton.setOnAction(event -> stageHandler.setDefaultView());

        TextArea messagesArea = stageHandler.getMessagesArea();

        VBox layout = new VBox(15, welcomeLabel, addUserButton, changePasswordButton, addDrugButton, logoutButton, messagesArea);
        layout.setPadding(new Insets(15));
        layout.setSpacing(10);

        return layout;
    }

    private void openChangePasswordScene() {
        VBox passwordLayout = new VBox(15);
        passwordLayout.setPadding(new Insets(15));

        Label instructionLabel = new Label("Enter New Password:");
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField newPasswordField = new TextField();
        newPasswordField.setPromptText("New Password");
        newPasswordField.setStyle("-fx-font-size: 14px;");

        Button submitButton = new Button("Change Password");
        submitButton.setStyle("-fx-font-size: 14px;");

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-font-size: 14px;");

        submitButton.setOnAction(event -> {
            String passwordText = newPasswordField.getText();
            if (passwordText.isEmpty()) {
                stageHandler.displayMessage("Please enter a new password.");
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonRequestNode = objectMapper.createObjectNode();
                jsonRequestNode.put("type", "changePassword");
                jsonRequestNode.put("password", passwordText);
                jsonRequestNode.put("id", getId());
                clientHandler.sendMessage(jsonRequestNode.toString());
                stageHandler.setScene(new Scene(generateLayout(), 400, 300));
            }
        });

        cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));

        passwordLayout.getChildren().addAll(instructionLabel, newPasswordField, submitButton, cancelButton);

        Scene passwordScene = new Scene(passwordLayout, 400, 300);
        stageHandler.setScene(passwordScene);
    }

    private void openAddUserScene() {
        VBox addUserLayout = new VBox(15);
        addUserLayout.setPadding(new Insets(15));

        Label peselLabel = new Label("Enter User PESEL:");
        peselLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField peselField = new TextField();
        peselField.setPromptText("PESEL");
        peselField.setStyle("-fx-font-size: 14px;");

        Label passwordLabel = new Label("Enter User Password:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-font-size: 14px;");

        Label nameLabel = new Label("Enter User Name:");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setStyle("-fx-font-size: 14px;");

        Label surnameLabel = new Label("Enter User Surname:");
        surnameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField surnameField = new TextField();
        surnameField.setPromptText("Surname");
        surnameField.setStyle("-fx-font-size: 14px;");

        ComboBox<String> userTypeComboBox = new ComboBox<>();
        userTypeComboBox.getItems().addAll("Doctor", "Pharmacist", "Patient", "Admin");
        userTypeComboBox.setPromptText("Choose a User Type");
        userTypeComboBox.setStyle("-fx-font-size: 14px;");

        Button submitButton = new Button("Add User");
        submitButton.setStyle("-fx-font-size: 14px;");

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-font-size: 14px;");

        submitButton.setOnAction(event -> {
            if (peselField.getText().isEmpty() || passwordField.getText().isEmpty() || nameField.getText().isEmpty() || surnameField.getText().isEmpty() || userTypeComboBox.getValue() == null) {
                stageHandler.displayMessage("Please fill in all fields.");
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonRequestNode = objectMapper.createObjectNode();
                jsonRequestNode.put("type", "createUser");
                jsonRequestNode.put("pesel", peselField.getText());
                jsonRequestNode.put("password", passwordField.getText());
                jsonRequestNode.put("name", nameField.getText());
                jsonRequestNode.put("surname", surnameField.getText());
                jsonRequestNode.put("usertype", userTypeComboBox.getValue());
                clientHandler.sendMessage(jsonRequestNode.toString());
                stageHandler.setScene(new Scene(generateLayout(), 400, 300));
            }
        });

        cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));

        addUserLayout.getChildren().addAll(peselLabel, peselField, passwordLabel, passwordField, nameLabel, nameField, surnameLabel, surnameField, userTypeComboBox, submitButton, cancelButton);

        Scene addUserScene = new Scene(addUserLayout, 400, 600);
        stageHandler.setScene(addUserScene);
    }

    private void openAddDrugScene() {
        VBox addDrugLayout = new VBox(15);
        addDrugLayout.setPadding(new Insets(15));

        Label drugNameLabel = new Label("Enter Drug Name:");
        drugNameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField drugNameField = new TextField();
        drugNameField.setPromptText("Drug Name");
        drugNameField.setStyle("-fx-font-size: 14px;");

        Label descriptionLabel = new Label("Enter Description:");
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        descriptionField.setStyle("-fx-font-size: 14px;");

        Label priceLabel = new Label("Enter Drug Price:");
        priceLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        priceField.setStyle("-fx-font-size: 14px;");

        Button submitButton = new Button("Add Drug");
        submitButton.setStyle("-fx-font-size: 14px;");

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-font-size: 14px;");

        submitButton.setOnAction(event -> {
            if (drugNameField.getText().isEmpty() || descriptionField.getText().isEmpty() || priceField.getText().isEmpty()) {
                stageHandler.displayMessage("Please fill in all fields.");
            } else if (!isDouble(priceField.getText())) {
                stageHandler.displayMessage("Price must be a number.");
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonRequestNode = objectMapper.createObjectNode();
                jsonRequestNode.put("type", "addDrugToDb");
                jsonRequestNode.put("drugName", drugNameField.getText());
                jsonRequestNode.put("description", descriptionField.getText());
                jsonRequestNode.put("price", priceField.getText());
                clientHandler.sendMessage(jsonRequestNode.toString());
                stageHandler.setScene(new Scene(generateLayout(), 400, 300));
            }
        });

        cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));

        addDrugLayout.getChildren().addAll(drugNameLabel, drugNameField, descriptionLabel, descriptionField, priceLabel, priceField, submitButton, cancelButton);

        Scene addDrugScene = new Scene(addDrugLayout, 400, 600);
        stageHandler.setScene(addDrugScene);
    }

    private boolean isDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

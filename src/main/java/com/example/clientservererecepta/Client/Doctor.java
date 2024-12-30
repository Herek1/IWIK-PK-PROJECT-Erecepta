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

public class Doctor extends User {
    private final ClientHandler clientHandler;
    private final StageHandler stageHandler;

    public Doctor(int id, String name, String surname, ClientHandler clientHandler, StageHandler stageHandler) {
        super(id, name, surname);
        this.clientHandler = clientHandler;
        this.stageHandler = stageHandler;
    }

    @Override
    public VBox generateLayout() {
        Label welcomeLabel = new Label("Welcome, " + toString());
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button createPrescriptionButton = new Button("Create Prescription");
        createPrescriptionButton.setMaxWidth(Double.MAX_VALUE);
        createPrescriptionButton.setStyle("-fx-font-size: 14px;");
        createPrescriptionButton.setOnAction(event -> openCreatePrescriptionScene());

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setMaxWidth(Double.MAX_VALUE);
        changePasswordButton.setStyle("-fx-font-size: 14px;");
        changePasswordButton.setOnAction(event -> openChangePasswordScene());

        Button logoutButton = new Button("Log Out");
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setStyle("-fx-font-size: 14px;");
        logoutButton.setOnAction(event -> stageHandler.setDefaultView());

        TextArea messagesArea = stageHandler.getMessagesArea();

        VBox layout = new VBox(15, welcomeLabel, createPrescriptionButton, changePasswordButton, logoutButton, messagesArea);
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

        Button sendRequestButton = new Button("Change Password");
        sendRequestButton.setStyle("-fx-font-size: 14px;");

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-font-size: 14px;");

        sendRequestButton.setOnAction(event -> {
            String passwordText = newPasswordField.getText();
            if (passwordText.isEmpty()) {
                stageHandler.displayMessage("Please enter a new password.");
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonResponseNode = objectMapper.createObjectNode();
                jsonResponseNode.put("type", "changePassword");
                jsonResponseNode.put("password", passwordText);
                jsonResponseNode.put("id", getId());
                clientHandler.sendMessage(jsonResponseNode.toString());
                stageHandler.setScene(new Scene(generateLayout(), 400, 300));
            }
        });

        cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));

        passwordLayout.getChildren().addAll(instructionLabel, newPasswordField, sendRequestButton, cancelButton);

        Scene passwordScene = new Scene(passwordLayout, 400, 300);
        stageHandler.setScene(passwordScene);
    }

    private void openCreatePrescriptionScene() {
        VBox prescriptionLayout = new VBox(15);
        prescriptionLayout.setPadding(new Insets(15));

        Label peselLabel = new Label("Enter Patient PESEL:");
        peselLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField peselField = new TextField();
        peselField.setPromptText("Patient PESEL");
        peselField.setStyle("-fx-font-size: 14px;");

        Label drugLabel = new Label("Enter Drug Name:");
        drugLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField drugNameField = new TextField();
        drugNameField.setPromptText("Drug Name");
        drugNameField.setStyle("-fx-font-size: 14px;");

        Label drugAmountLabel = new Label("Enter Amount:");
        drugAmountLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField drugAmountField = new TextField();
        drugAmountField.setPromptText("Amount");
        drugAmountField.setStyle("-fx-font-size: 14px;");

        Button addDrugButton = new Button("Add Drug");
        addDrugButton.setStyle("-fx-font-size: 14px;");

        TextArea prescribedDrugsArea = new TextArea();
        prescribedDrugsArea.setEditable(false);
        prescribedDrugsArea.setWrapText(true);
        prescribedDrugsArea.setStyle("-fx-font-size: 14px;");
        prescribedDrugsArea.setPrefHeight(200);

        List<HashMap<String, String>> drugs = new ArrayList<>();

        addDrugButton.setOnAction(event -> {
            String drugName = drugNameField.getText();
            String amount = drugAmountField.getText();

            if (drugName.isEmpty() || amount.isEmpty()) {
                stageHandler.displayMessage("Please fill in all fields.");
            } else {
                HashMap<String, String> drugEntry = new HashMap<>();
                drugEntry.put("drugName", drugName);
                drugEntry.put("amount", amount);
                drugs.add(drugEntry);

                prescribedDrugsArea.appendText("Drug: " + drugName + ", Amount: " + amount + "\n");
                drugNameField.clear();
                drugAmountField.clear();
            }
        });

        Button submitButton = new Button("Submit Prescription");
        submitButton.setStyle("-fx-font-size: 14px;");
        submitButton.setOnAction(event -> {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonRequestNode = objectMapper.createObjectNode();
            jsonRequestNode.put("type", "addPrescription");
            jsonRequestNode.put("patient", peselField.getText());
            jsonRequestNode.put("doctor", this.getId());
            jsonRequestNode.set("drugs", objectMapper.valueToTree(drugs));
            clientHandler.sendMessage(jsonRequestNode.toString());
            stageHandler.setScene(new Scene(generateLayout(), 400, 300));
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-font-size: 14px;");
        cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));

        prescriptionLayout.getChildren().addAll(peselLabel, peselField, drugLabel, drugNameField, drugAmountLabel, drugAmountField, addDrugButton, prescribedDrugsArea, submitButton, cancelButton);

        Scene prescriptionScene = new Scene(prescriptionLayout, 400, 600);
        stageHandler.setScene(prescriptionScene);
    }
}

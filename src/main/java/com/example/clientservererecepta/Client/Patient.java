package com.example.clientservererecepta.Client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class Patient extends User {
    private final ClientHandler clientHandler;
    private final StageHandler stageHandler;

    public Patient(int id, String name, String surname, ClientHandler clientHandler, StageHandler stageHandler) {
        super(id, name, surname);
        this.clientHandler = clientHandler;
        this.stageHandler = stageHandler;
    }

    @Override
    public VBox generateLayout() {
        Label welcomeLabel = new Label("Welcome, " + toString());

        // Button to check prescriptions
        Button checkPrescriptionsButton = new Button("Check Prescriptions");
        checkPrescriptionsButton.setOnAction(event -> {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonRequestNode = objectMapper.createObjectNode();
            jsonRequestNode.put("type", "getPrescriptions");
            jsonRequestNode.put("id",getId());
            clientHandler.sendMessage(jsonRequestNode.toString());
        });

        Button checkDrugAvailabilityButton = new Button("Check Drug Availability");
        checkDrugAvailabilityButton.setOnAction(event ->{
                openDrugAvailabilityScene();
        });
        Button changePassword = new Button("Change Password");
        changePassword.setOnAction(event -> {
            openChangePasswordScene();
        });


        // Use the shared messagesArea from StageHandler
        TextArea messagesArea = stageHandler.getMessagesArea();
        stageHandler.displayMessage("");
        return new VBox(10, welcomeLabel, checkPrescriptionsButton, checkDrugAvailabilityButton,changePassword, messagesArea);
    }

    public void updatePrescriptions(JsonNode response) {
        JsonNode dataArray = response.get("data");
        StringBuilder finalInfo = new StringBuilder();
        if (dataArray != null && dataArray.isArray() && dataArray.size() > 0) {
            for (JsonNode prescriptionNode : dataArray) {
                String patientName = prescriptionNode.has("patient") ? prescriptionNode.get("patient").asText() : "Unknown Patient";
                String code = prescriptionNode.has("code") ? prescriptionNode.get("code").asText() : "Unknown Code";
                String drugs = prescriptionNode.has("drugs") ? prescriptionNode.get("drugs").asText() : "No Drugs Listed";
                String date = prescriptionNode.has("date") ? prescriptionNode.get("date").asText() : "Unknown Date";

                StringBuilder prescriptionInfo = new StringBuilder();
                prescriptionInfo.append("Prescription for: ").append(patientName).append("\n")
                        .append("Code: ").append(code).append("\n")
                        .append("Date: ").append(date).append("\n")
                        .append("Drugs:\n").append(drugs).append("\n")
                        .append("-----\n");

                // Use StageHandler to display the prescription info
                finalInfo.append(prescriptionInfo);
            }
            stageHandler.displayMessage(finalInfo.toString());
        } else {
            stageHandler.displayMessage("No prescriptions found.");
        }
    }

    private void openDrugAvailabilityScene() {
        // New scene components
        VBox drugLayout = new VBox(10);
        Label instructionLabel = new Label("Enter the drug name to check availability:");
        TextField drugNameField = new TextField();
        Button sendRequestButton = new Button("Check Availability");
        Button cancelButton = new Button("Cancel");
        TextArea drugResultsArea = stageHandler.getMessagesArea();
        drugResultsArea.setEditable(false); // Make results area read-only

        // Send request to server
         sendRequestButton.setOnAction(event -> {
            String drugName = drugNameField.getText();
            if (drugName.isEmpty()) {
                drugResultsArea.setText("Please enter a drug name.");
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonRequestNode = objectMapper.createObjectNode();
                jsonRequestNode.put("type", "checkDrugAvailability");
                jsonRequestNode.put("drugName",drugName);

                clientHandler.sendMessage(jsonRequestNode.toString());
                drugResultsArea.setText("Checking availability for: " + drugName);
            }
        });

        // Cancel button to return to the main layout
        cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));

        // Add components to layout
        drugLayout.getChildren().addAll(instructionLabel, drugNameField, sendRequestButton, drugResultsArea, cancelButton);

        // Set the new scene
        Scene drugScene = new Scene(drugLayout, 400, 300);
        stageHandler.setScene(drugScene);
        stageHandler.displayMessage("");
    }

    public void updateDrugAvailability(JsonNode response) {
        // Assuming response is a simple message from the server
        JsonNode dataArray = response.get("data");
        StringBuilder finalInfo = new StringBuilder();
        if (dataArray != null && dataArray.isArray() && dataArray.size() > 0) {
            for (JsonNode prescriptionNode : dataArray) {
                if (!prescriptionNode.has("address")){
                    continue;
                }
                String address = prescriptionNode.has("address") ? prescriptionNode.get("address").asText() : "Unknown address";
                String amount = prescriptionNode.has("amount") ? prescriptionNode.get("amount").asText() : "Unknown amount";

                StringBuilder drugInfo = new StringBuilder();
                drugInfo.append("pharmacy: ").append(address).append(" | ")
                        .append("amount: ").append(amount).append("\n")
                        .append("\n");

                finalInfo.append(drugInfo);
            }
            stageHandler.displayMessage(finalInfo.toString());
        } else {
            stageHandler.displayMessage("No drug found.");
        }
    }

    private void openChangePasswordScene() {
        VBox passwordLayout = new VBox(10);
        Label instructionLabel = new Label("Enter new password");
        TextField newPassowrd = new TextField();
        Button sendRequestButton = new Button("Change password");
        Button cancelButton = new Button("Cancel");
        TextArea drugResultsArea = stageHandler.getMessagesArea();
        drugResultsArea.setEditable(false); // Make results area read-only

        // Send request to server
        sendRequestButton.setOnAction(event -> {
            String passwordText = newPassowrd.getText();
            if (passwordText.isEmpty()) {
                drugResultsArea.setText("Please enter a new password.");
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonResponseNode = objectMapper.createObjectNode();
                jsonResponseNode.put("type", "changePassword");
                jsonResponseNode.put("password",passwordText);
                jsonResponseNode.put("id",getId());
                clientHandler.sendMessage(jsonResponseNode.toString());
                stageHandler.setScene(new Scene(generateLayout(), 400, 300));
            }
        });

        cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));
        passwordLayout.getChildren().addAll(instructionLabel, newPassowrd, sendRequestButton, drugResultsArea, cancelButton);

        // Set the new scene
        Scene passwordScene = new Scene(passwordLayout, 400, 300);
        stageHandler.setScene(passwordScene);
        stageHandler.displayMessage("");
    }
}
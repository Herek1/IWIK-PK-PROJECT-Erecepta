package com.example.clientservererecepta.Client;

import com.fasterxml.jackson.databind.JsonNode;
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
            clientHandler.sendMessage("getPrescriptions;" + getId());
        });

        Button checkDrugAvailabilityButton = new Button("Check Drug Availability");
        checkDrugAvailabilityButton.setOnAction(event ->{
                openDrugAvailabilityScene();
        });


        // Use the shared messagesArea from StageHandler
        TextArea messagesArea = stageHandler.getMessagesArea();

        return new VBox(10, welcomeLabel, messagesArea, checkPrescriptionsButton, checkDrugAvailabilityButton);
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
                String status = prescriptionNode.has("status") ? prescriptionNode.get("status").asText() : "No Status";

                StringBuilder prescriptionInfo = new StringBuilder();
                prescriptionInfo.append("Prescription for: ").append(patientName).append("\n")
                        .append("Code: ").append(code).append("\n")
                        .append("Date: ").append(date).append("\n")
                        .append("Status: ").append(status).append("\n")
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
                clientHandler.sendMessage("checkDrugAvailability;" + drugName);
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
        System.out.println("test");
        // Assuming response is a simple message from the server
        JsonNode dataArray = response.get("data");
        StringBuilder finalInfo = new StringBuilder();
        if (dataArray != null && dataArray.isArray() && dataArray.size() > 0) {
            for (JsonNode prescriptionNode : dataArray) {
                String drugName = prescriptionNode.has("drugName") ? prescriptionNode.get("drugName").asText() : "Unknown drug";
                String address = prescriptionNode.has("address") ? prescriptionNode.get("address").asText() : "Unknown address";
                String amount = prescriptionNode.has("amount") ? prescriptionNode.get("amount").asText() : "Unknown amount";

                if(drugName.equals("Unknown drug")){
                    continue;
                }

                StringBuilder drugInfo = new StringBuilder();
                drugInfo.append("Drug: ").append(drugName).append(" | ")
                        .append("pharmacy: ").append(address).append(" | ")
                        .append("amount: ").append(amount).append("\n")
                        .append("-----\n");

                // Use StageHandler to display the prescription info
                finalInfo.append(drugInfo);
            }
            stageHandler.displayMessage(finalInfo.toString());
        } else {
            stageHandler.displayMessage("No drug found.");
        }
    }
}
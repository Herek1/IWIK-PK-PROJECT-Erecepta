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
            jsonRequestNode.put("type", "getPrescriptionsPatient");
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

        Button logout = new Button("Log out");
        logout.setOnAction(event ->{
                stageHandler.setDefaultView();
         });


        // Use the shared messagesArea from StageHandler
        TextArea messagesArea = stageHandler.getMessagesArea();
        stageHandler.displayMessage("");
        return new VBox(10, welcomeLabel, checkPrescriptionsButton, checkDrugAvailabilityButton,changePassword,logout , messagesArea);
    }

    public void updatePrescriptions(JsonNode response) {
        JsonNode dataArray = response.get("data");
        StringBuilder finalInfo = new StringBuilder();
        if (dataArray != null && dataArray.isArray() && dataArray.size() > 0) {
            for (int i = 1; i < dataArray.size(); i++) {
                JsonNode prescriptionNode = dataArray.get(i);

                // Safe retrieval of fields with default values in case they're missing
                String code = prescriptionNode.has("recipeId") ? prescriptionNode.get("recipeId").asText() : "N/A";
                String doctorName = prescriptionNode.has("doctorName") ? prescriptionNode.get("doctorName").asText() : "Unknown";
                String doctorSurname = prescriptionNode.has("doctorSurname") ? prescriptionNode.get("doctorSurname").asText() : "Unknown";
                String date = prescriptionNode.has("date") ? prescriptionNode.get("date").asText() : "N/A";

                // Initialize StringBuilder to store prescription info
                StringBuilder prescriptionInfo = new StringBuilder();
                prescriptionInfo.append("Prescription: ").append(code).append("\n")
                        .append("Date: ").append(date).append("\n")
                        .append("Doctor: ").append(doctorName).append(" ").append(doctorSurname).append("\n");

                // Get the drugs node and process each drug
                JsonNode drugsArray = prescriptionNode.get("drugs");
                if (drugsArray != null && drugsArray.isArray()) {
                    StringBuilder drugsInfo = new StringBuilder();
                    for (int j = 1; j < drugsArray.size(); j++) {
                        JsonNode drugNode = drugsArray.get(j);
                        // Safe retrieval of drug fields
                        String drugName = drugNode.has("drugName") ? drugNode.get("drugName").asText() : "Unknown Drug";
                        String amount = drugNode.has("amount") ? drugNode.get("amount").asText() : "N/A";
                        String fulfillMethod = drugNode.has("fulfillMethod") ? drugNode.get("fulfillMethod").asText() : "Unknown";

                        // Display only if fulfillMethod is not "Sold"
                        if (!"Sold".equals(fulfillMethod)) {
                            drugsInfo.append("   ").append(drugName)
                                    .append(", Amount: ").append(amount)
                                    .append("\n");
                        }
                    }

                    // If there are drugs info, append it
                    if (drugsInfo.length() > 0) {
                        prescriptionInfo.append("Drugs:\n").append(drugsInfo);
                    } else {
                        prescriptionInfo.append("No drugs to display.\n");
                    }
                }

                prescriptionInfo.append("\n");
                finalInfo.append(prescriptionInfo);
            }

            // Display the final message using StageHandler
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
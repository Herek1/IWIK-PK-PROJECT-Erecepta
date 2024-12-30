package com.example.clientservererecepta.Client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
        stageHandler.displayMessage("");
        Label welcomeLabel = new Label("Welcome, " + toString());
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button checkPrescriptionsButton = new Button("Check Prescriptions");
        checkPrescriptionsButton.setMaxWidth(Double.MAX_VALUE);
        checkPrescriptionsButton.setOnAction(event -> {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonRequestNode = objectMapper.createObjectNode();
            jsonRequestNode.put("type", "getPrescriptionsPatient");
            jsonRequestNode.put("id", getId());
            clientHandler.sendMessage(jsonRequestNode.toString());
        });

        Button checkDrugAvailabilityButton = new Button("Check Drug Availability");
        checkDrugAvailabilityButton.setMaxWidth(Double.MAX_VALUE);
        checkDrugAvailabilityButton.setOnAction(event -> openDrugAvailabilityScene());

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setMaxWidth(Double.MAX_VALUE);
        changePasswordButton.setOnAction(event -> openChangePasswordScene());

        Button logoutButton = new Button("Log Out");
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setOnAction(event -> stageHandler.setDefaultView());

        TextArea messagesArea = stageHandler.getMessagesArea();

        VBox layout = new VBox(15, welcomeLabel, checkPrescriptionsButton, checkDrugAvailabilityButton, changePasswordButton, logoutButton, messagesArea);
        layout.setPadding(new Insets(15));
        layout.setSpacing(10);

        return layout;
    }

    public void updatePrescriptions(JsonNode response) {
        JsonNode dataArray = response.get("data");
        StringBuilder finalInfo = new StringBuilder();
        if (dataArray != null && dataArray.isArray() && dataArray.size() > 0) {
            for (int i = 1; i < dataArray.size(); i++) {
                JsonNode prescriptionNode = dataArray.get(i);

                String code = prescriptionNode.has("recipeId") ? prescriptionNode.get("recipeId").asText() : "N/A";
                String doctorName = prescriptionNode.has("doctorName") ? prescriptionNode.get("doctorName").asText() : "Unknown";
                String doctorSurname = prescriptionNode.has("doctorSurname") ? prescriptionNode.get("doctorSurname").asText() : "Unknown";
                String date = prescriptionNode.has("date") ? prescriptionNode.get("date").asText() : "N/A";

                StringBuilder prescriptionInfo = new StringBuilder();
                prescriptionInfo.append("Prescription: ").append(code).append("\n")
                        .append("Date: ").append(date).append("\n")
                        .append("Doctor: ").append(doctorName).append(" ").append(doctorSurname).append("\n");

                JsonNode drugsArray = prescriptionNode.get("drugs");
                if (drugsArray != null && drugsArray.isArray()) {
                    StringBuilder drugsInfo = new StringBuilder();
                    for (int j = 1; j < drugsArray.size(); j++) {
                        JsonNode drugNode = drugsArray.get(j);
                        String drugName = drugNode.has("drugName") ? drugNode.get("drugName").asText() : "Unknown Drug";
                        String amount = drugNode.has("amount") ? drugNode.get("amount").asText() : "N/A";
                        String fulfillMethod = drugNode.has("fulfillMethod") ? drugNode.get("fulfillMethod").asText() : "Unknown";

                        if (!"Sold".equals(fulfillMethod)) {
                            drugsInfo.append("   ").append(drugName)
                                    .append(", Amount: ").append(amount)
                                    .append("\n");
                        }
                    }
                    if (drugsInfo.length() > 0) {
                        prescriptionInfo.append("Drugs:\n").append(drugsInfo);
                    } else {
                        prescriptionInfo.append("No drugs to display.\n");
                    }
                }

                prescriptionInfo.append("\n");
                finalInfo.append(prescriptionInfo);
            }
            stageHandler.displayMessage(finalInfo.toString());
        } else {
            stageHandler.displayMessage("No prescriptions found.");
        }
    }

    private void openDrugAvailabilityScene() {
        stageHandler.displayMessage("");
        VBox drugLayout = new VBox(10);
        drugLayout.setPadding(new Insets(15));

        Label instructionLabel = new Label("Enter the drug name to check availability:");
        instructionLabel.setStyle("-fx-font-size: 14px;");

        TextField drugNameField = new TextField();
        Button sendRequestButton = new Button("Check Availability");
        sendRequestButton.setMaxWidth(Double.MAX_VALUE);
        Button cancelButton = new Button("Cancel");
        cancelButton.setMaxWidth(Double.MAX_VALUE);
        TextArea drugResultsArea = stageHandler.getMessagesArea();
        drugResultsArea.setEditable(false);

        sendRequestButton.setOnAction(event -> {
            String drugName = drugNameField.getText();
            if (drugName.isEmpty()) {
                drugResultsArea.setText("Please enter a drug name.");
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonRequestNode = objectMapper.createObjectNode();
                jsonRequestNode.put("type", "checkDrugAvailability");
                jsonRequestNode.put("drugName", drugName);
                clientHandler.sendMessage(jsonRequestNode.toString());
                drugResultsArea.setText("Checking availability for: " + drugName);
            }
        });

        cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));

        drugLayout.getChildren().addAll(instructionLabel, drugNameField, sendRequestButton, drugResultsArea, cancelButton);
        Scene drugScene = new Scene(drugLayout, 400, 400);
        stageHandler.setScene(drugScene);
    }

    public void updateDrugAvailability(JsonNode response) {
        JsonNode dataArray = response.get("data");
        StringBuilder finalInfo = new StringBuilder();
        if (dataArray != null && dataArray.isArray() && dataArray.size() > 0) {
            for (JsonNode prescriptionNode : dataArray) {
                if (!prescriptionNode.has("address")) {
                    continue;
                }
                String address = prescriptionNode.has("address") ? prescriptionNode.get("address").asText() : "Unknown address";
                String amount = prescriptionNode.has("amount") ? prescriptionNode.get("amount").asText() : "Unknown amount";

                StringBuilder drugInfo = new StringBuilder();
                drugInfo.append("Pharmacy: ").append(address).append(" | ")
                        .append("Amount: ").append(amount).append("\n\n");

                finalInfo.append(drugInfo);
            }
            stageHandler.displayMessage(finalInfo.toString());
        } else {
            stageHandler.displayMessage("No drug found.");
        }
    }

    private void openChangePasswordScene() {
        stageHandler.displayMessage("");
        VBox passwordLayout = new VBox(10);
        passwordLayout.setPadding(new Insets(15));

        Label instructionLabel = new Label("Enter new password:");
        instructionLabel.setStyle("-fx-font-size: 14px;");

        TextField newPasswordField = new TextField();
        Button sendRequestButton = new Button("Change Password");
        Button cancelButton = new Button("Cancel");

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
                stageHandler.displayMessage("Password changed successfully.");
            }
        });

        cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));

        sendRequestButton.setMaxWidth(Double.MAX_VALUE);
        cancelButton.setMaxWidth(Double.MAX_VALUE);

        passwordLayout.getChildren().addAll(instructionLabel, newPasswordField, sendRequestButton, cancelButton);
        Scene passwordScene = new Scene(passwordLayout, 400, 300);
        stageHandler.setScene(passwordScene);
    }
}

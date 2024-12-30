package com.example.clientservererecepta.Client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class Pharmacist extends User {
    private final ClientHandler clientHandler;
    private final StageHandler stageHandler;
    private String currentPrescription = "";

    public Pharmacist(int id, String name, String surname, ClientHandler clientHandler, StageHandler stageHandler) {
        super(id, name, surname);
        this.clientHandler = clientHandler;
        this.stageHandler = stageHandler;
    }

    @Override
    public VBox generateLayout() {
        stageHandler.displayMessage("");
        Label welcomeLabel = new Label("Welcome, " + toString());
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label prescriptionLabel = new Label("Prescription Number:");
        TextField prescriptionNumberField = new TextField();
        prescriptionNumberField.setPromptText("Enter prescription number");

        Button checkPrescriptionsButton = new Button("Check Prescriptions");
        checkPrescriptionsButton.setMaxWidth(Double.MAX_VALUE);
        checkPrescriptionsButton.setOnAction(event -> {
            if (prescriptionNumberField.getText().isEmpty()) {
                stageHandler.displayMessage("Please enter a prescription number.");
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonRequestNode = objectMapper.createObjectNode();
                currentPrescription = prescriptionNumberField.getText();
                jsonRequestNode.put("type", "getPrescriptionsPharmacist");
                jsonRequestNode.put("id", currentPrescription);
                clientHandler.sendMessage(jsonRequestNode.toString());
            }
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

        VBox layout = new VBox(15, welcomeLabel, prescriptionLabel, prescriptionNumberField, checkPrescriptionsButton, checkDrugAvailabilityButton, changePasswordButton, logoutButton, messagesArea);
        layout.setPadding(new Insets(15));
        layout.setSpacing(10);

        return layout;
    }

    private void openChangePasswordScene() {
        VBox passwordLayout = new VBox(10);
        passwordLayout.setPadding(new Insets(15));

        Label instructionLabel = new Label("Enter new password:");
        instructionLabel.setStyle("-fx-font-size: 14px;");

        TextField newPasswordField = new TextField();
        newPasswordField.setPromptText("New Password");

        Button sendRequestButton = new Button("Change Password");
        sendRequestButton.setMaxWidth(Double.MAX_VALUE);

        Button cancelButton = new Button("Cancel");
        cancelButton.setMaxWidth(Double.MAX_VALUE);
        cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));

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

        passwordLayout.getChildren().addAll(instructionLabel, newPasswordField, sendRequestButton, cancelButton);

        Scene passwordScene = new Scene(passwordLayout, 400, 300);
        stageHandler.setScene(passwordScene);
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

    public void updatePrescriptions(JsonNode response) {
        JsonNode dataArray = response.get("data");
        VBox prescriptionsLayout = new VBox(10);
        prescriptionsLayout.setPadding(new Insets(15));

        if (dataArray != null && dataArray.isArray() && dataArray.size() > 1) {
            for (int i = 1; i < dataArray.size(); i++) {
                JsonNode prescriptionNode = dataArray.get(i);

                String fulfillMethod = prescriptionNode.get("fulfillMethod").asText();
                if ("Sold".equals(fulfillMethod)) {
                    continue;
                }

                String drugName = prescriptionNode.get("drugName").asText();
                String amount = prescriptionNode.get("amount").asText();

                Label drugLabel = new Label("Drug: " + drugName + " | Amount: " + amount);
                Button sellButton = new Button("Sell");

                sellButton.setOnAction(event -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ObjectNode jsonRequestNode = objectMapper.createObjectNode();
                    jsonRequestNode.put("type", "sellDrug");
                    jsonRequestNode.put("drugName", drugName);
                    jsonRequestNode.put("recipeId", currentPrescription);
                    jsonRequestNode.put("pharmacistId", getId());
                    clientHandler.sendMessage(jsonRequestNode.toString());
                });

                prescriptionsLayout.getChildren().addAll(drugLabel, sellButton);
            }

            Button cancelButton = new Button("Cancel");
            cancelButton.setMaxWidth(Double.MAX_VALUE);
            cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));

            prescriptionsLayout.getChildren().addAll(cancelButton);

            Platform.runLater(() -> {
                Scene prescriptionScene = new Scene(prescriptionsLayout, 400, 600);
                stageHandler.setScene(prescriptionScene);
            });
        } else {
            stageHandler.displayMessage("No prescriptions found.");
        }
    }

    public void updateDrugSold(JsonNode response) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonRequestNode = objectMapper.createObjectNode();
        jsonRequestNode.put("type", "getPrescriptionsPharmacist");
        jsonRequestNode.put("id", currentPrescription);
        clientHandler.sendMessage(jsonRequestNode.toString());
    }

    public void updateDrugAvailability(JsonNode response) {
        JsonNode dataArray = response.get("data");
        StringBuilder finalInfo = new StringBuilder();

        if (dataArray != null && dataArray.isArray() && dataArray.size() > 0) {
            for (JsonNode prescriptionNode : dataArray) {
                if (!prescriptionNode.has("address")) {
                    continue;
                }
                String address = prescriptionNode.get("address").asText("Unknown address");
                String amount = prescriptionNode.get("amount").asText("Unknown amount");

                finalInfo.append("Pharmacy: ").append(address).append(" | Amount: ").append(amount).append("\n\n");
            }
            stageHandler.displayMessage(finalInfo.toString());
        } else {
            stageHandler.displayMessage("No drug found.");
        }
    }
}

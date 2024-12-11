package com.example.clientservererecepta.Client;

import com.example.clientservererecepta.Client.Util.ShowAlert;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Pharmacist extends User {
    private final ClientHandler clientHandler;
    private final StageHandler stageHandler;

    public Pharmacist(int id, String name, String surname, ClientHandler clientHandler, StageHandler stageHandler) {
        super(id, name, surname);
        this.clientHandler = clientHandler;
        this.stageHandler = stageHandler;
    }

    @Override
    public VBox generateLayout() {
        Label welcomeLabel = new Label("Welcome, " + toString());

        // Button to check prescriptions
        Button checkPrescriptionsButton = new Button("Check Prescriptions");
        Label pesel = new Label("Pesel pacjenta");
        TextField peselTextField = new TextField();
        checkPrescriptionsButton.setOnAction(event -> {
            if(peselTextField.getText().isEmpty()){
                ShowAlert.error("You did not fill all required fields");
            }else{
            clientHandler.sendMessage("getPrescriptions;" + peselTextField.getText());}

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

        return new VBox(10, welcomeLabel, pesel, peselTextField, checkPrescriptionsButton, checkDrugAvailabilityButton,changePassword, messagesArea);
    }

    private void openChangePasswordScene() {
        VBox drugLayout = new VBox(10);
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
                clientHandler.sendMessage("changePassword;" + passwordText + ";"+getId());
                stageHandler.setScene(new Scene(generateLayout(), 400, 300));
            }
        });

        // Cancel button to return to the main layout
        cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));

        // Add components to layout
        drugLayout.getChildren().addAll(instructionLabel, newPassowrd, sendRequestButton, drugResultsArea, cancelButton);

        // Set the new scene
        Scene passwordScene = new Scene(drugLayout, 400, 300);
        stageHandler.setScene(passwordScene);
        stageHandler.displayMessage("");
    }

    private void openDrugAvailabilityScene() {
        // New scene components
        VBox drugLayout = new VBox(10);
        Label instructionLabel = new Label("Enter the drug name to check availability:");
        TextField drugNameField = new TextField();
        Label locationLabel = new Label("Enter pharmacy address");
        TextField locationField = new TextField();
        Button sendRequestButton = new Button("Check Availability");
        Button cancelButton = new Button("Cancel");
        TextArea drugResultsArea = stageHandler.getMessagesArea();
        drugResultsArea.setEditable(false); // Make results area read-only

        // Send request to server
        sendRequestButton.setOnAction(event -> {
            String drugName = drugNameField.getText();
            String location = locationField.getText();
            if (drugName.isEmpty() || location.isEmpty()) {
                drugResultsArea.setText("Please enter a drug name.");
            } else {
                clientHandler.sendMessage("checkDrugAvailability;" + drugName+";"+location);
                drugResultsArea.setText("Checking availability for: " + drugName);
            }
        });

        // Cancel button to return to the main layout
        cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));

        // Add components to layout
        drugLayout.getChildren().addAll(instructionLabel, drugNameField, locationLabel, locationField, sendRequestButton, drugResultsArea, cancelButton);

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

    public void updatePrescriptions(JsonNode response) {
        JsonNode dataArray = response.get("data");
        VBox prescriptionsLayout = new VBox(10); // Use VBox to organize drug entries vertically
        prescriptionsLayout.setSpacing(10);

        if (dataArray != null && dataArray.isArray() && dataArray.size() > 0) {
            for (JsonNode prescriptionNode : dataArray) {
//                String drugName = prescriptionNode.has("drugName") ? prescriptionNode.get("drugName").asText() : "Unknown Drug";
                String patientName = prescriptionNode.has("patient") ? prescriptionNode.get("patient").asText() : "Unknown Patient";
//                String amount = prescriptionNode.has("amount") ? prescriptionNode.get("amount").asText() : "Unknown Amount";
//                String prescriptionCode = prescriptionNode.has("code") ? prescriptionNode.get("code").asText() : "Unknown Code";
                String drugs = prescriptionNode.has("drugs") ? prescriptionNode.get("drugs").asText() : "No Drugs Listed";

                // Create UI components for each drug
                Label drugLabel = new Label("Drug: " + drugs + " | Patient: " + patientName);
                Button sellButton = new Button("Sell");

                // Action for the sell button
                sellButton.setOnAction(event -> {
                    // Call method to handle selling the drug
                    sellDrug("test","test","test");
                });

                // Add the drug info and button to the layout
                prescriptionsLayout.getChildren().addAll(drugLabel, sellButton);
            }
            Button cancelButton = new Button("Cancel");
            cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));

            prescriptionsLayout.getChildren().addAll(cancelButton);
            // Set the layout into the scene using StageHandler
            Platform.runLater(() -> {
                Scene prescriptionScene = new Scene(prescriptionsLayout, 400, 600);
                stageHandler.setScene(prescriptionScene);
            });
        } else {
            stageHandler.displayMessage("No prescriptions found.");
        }
    }
    private void sellDrug(String prescriptionCode, String drugName, String amount) {
        // Send a request to the server to update the database for the sold drug
        String message = String.format("sellDrug;%s;%s;%s", prescriptionCode, drugName, amount);
        clientHandler.sendMessage(message);

    }
}
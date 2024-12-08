package com.example.clientservererecepta.Client;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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

        // Button to manage appointments
        Button manageAppointmentsButton = new Button("Manage Appointments");
        manageAppointmentsButton.setOnAction(event -> {
            clientHandler.sendMessage("manageAppointments;" + getId());
        });

        // Use the shared messagesArea from StageHandler
        TextArea messagesArea = stageHandler.getMessagesArea();

        return new VBox(10, welcomeLabel, messagesArea, checkPrescriptionsButton, manageAppointmentsButton);
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
}

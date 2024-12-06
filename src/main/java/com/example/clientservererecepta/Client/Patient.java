package com.example.clientservererecepta.Client;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class Patient extends User {
    private final ClientHandler clientHandler;

    public Patient(int id, String name, String surname, ClientHandler clientHandler) {
        super(id, name, surname);
        this.clientHandler = clientHandler;
    }

    @Override
    public VBox generateLayout() {
        Label welcomeLabel = new Label("Welcome, " + toString());
        TextArea messagesArea = new TextArea();
        messagesArea.setEditable(false);

        // Button to check prescriptions
        Button checkPrescriptionsButton = new Button("Check Prescriptions");
        checkPrescriptionsButton.setOnAction(event -> {
            clientHandler.sendMessage("getPrescriptions;" + getId());
            messagesArea.appendText("Fetching prescriptions...\n");
        });

        // Button to manage appointments
        Button manageAppointmentsButton = new Button("Manage Appointments");
        manageAppointmentsButton.setOnAction(event -> {
            clientHandler.sendMessage("manageAppointments;" + getId());
            messagesArea.appendText("Opening appointment management...\n");
        });

        return new VBox(10, welcomeLabel, messagesArea, checkPrescriptionsButton, manageAppointmentsButton);
    }
}

package com.example.clientservererecepta.Client;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StageHandler {
    private final Stage stage;
    private final ClientHandler clientHandler; // Handles communication
    private TextArea messagesArea;
    private TextField login;
    private TextField password;

    public StageHandler(Stage stage, ClientHandler clientHandler) {
        this.stage = stage;
        this.clientHandler = clientHandler;
    }

    public void setDefaultView() {
        VBox defaultLayout = generateDefaultLayout();
        stage.setScene(new Scene(defaultLayout, 400, 300));
        stage.setTitle("E-Prescription System");
        stage.show();
    }

    public void switchToRoleView(String role, User user) {
        VBox layout;
        switch (role.toLowerCase()) {
            case "doctor":
                layout = generateDoctorLayout(); // No specific attributes needed
                break;
            case "pharmacist":
                layout = generatePharmacistLayout(); // No specific attributes needed
                break;
            case "patient":
                if (user instanceof Patient) {
                    layout = generatePatientLayout((Patient) user); // Cast to Patient
                } else {
                    layout = generateDefaultLayout(); // Fallback in case of mismatch
                    displayMessage("Error: Invalid user type for patient view.");
                }
                break;
            default:
                layout = generateDefaultLayout();
                displayMessage("Error: Unsupported role.");
                break;
        }
        stage.setScene(new Scene(layout, 400, 300));
    }


    private VBox generateDefaultLayout() {
        login = new TextField();
        login.setPromptText("Login");

        password = new TextField();
        password.setPromptText("Password");

        messagesArea = new TextArea();
        messagesArea.setEditable(false);

        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> sendLoginData());

        return new VBox(10, messagesArea, login, password, loginButton);
    }


    private VBox generateDoctorLayout() {
        messagesArea = new TextArea();
        messagesArea.setEditable(false);

        Button addPrescriptionButton = new Button("Add Prescription");
        addPrescriptionButton.setOnAction(event -> {
            messagesArea.appendText("Doctor is adding a prescription...\n");
        });

        return new VBox(10, messagesArea, addPrescriptionButton);
    }

    private VBox generatePharmacistLayout() {
        messagesArea = new TextArea();
        messagesArea.setEditable(false);

        Button processPrescriptionButton = new Button("Process Prescription");
        processPrescriptionButton.setOnAction(event -> {
            messagesArea.appendText("Pharmacist is processing a prescription...\n");
        });

        return new VBox(10, messagesArea, processPrescriptionButton);
    }

    private VBox generatePatientLayout(Patient patient) {
        Label welcomeLabel = new Label("Welcome, " + patient.getName() + " " + patient.getSurname());
        TextArea prescriptionsArea = new TextArea("Your prescriptions will appear here.");
        prescriptionsArea.setEditable(false);

        Button refreshButton = new Button("Refresh Prescriptions");
        refreshButton.setOnAction(event -> prescriptionsArea.appendText("\nRefreshing prescriptions..."));

        return new VBox(10, welcomeLabel, prescriptionsArea, refreshButton);
    }

    private void sendLoginData() {
        String loginData = login.getText() + ";" + password.getText();
        if (!loginData.isBlank()) {
            clientHandler.sendMessage(loginData);
        } else {
            messagesArea.appendText("Please enter login credentials.\n");
        }
    }
    public void displayMessage(String message) {
        if (messagesArea != null) {
            messagesArea.appendText(message + "\n");
        }
    }
}

package com.example.clientservererecepta.Client;

import com.example.clientservererecepta.Client.Util.ShowAlert;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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


        Button createPrescription = new Button("Create prescription");
        createPrescription.setOnAction(event -> {
            openCreatePrescriptionScene();
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

        return new VBox(10, welcomeLabel, createPrescription, changePassword, logout,messagesArea);
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
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonResponseNode = objectMapper.createObjectNode();
                jsonResponseNode.put("type", "changePassword");
                jsonResponseNode.put("password",passwordText);
                jsonResponseNode.put("id",getId());
                clientHandler.sendMessage(jsonResponseNode.toString());
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

    private void openCreatePrescriptionScene(){
        VBox drugLayout = new VBox(10);
        Label peselLabel = new Label("Eneter patient pesel");
        TextField pesel = new TextField();

        Label drugLabel = new Label("Enter drug name");
        TextField drugName = new TextField();
        Label drugAmmountLabel = new Label("Enter ammount");
        TextField amount = new TextField();

        Button addDrug = new Button("add drug");

        Button sendRequestButton = new Button("Submit");
        Button cancelButton = new Button("Cancel");
        TextArea prescribedDrugs = stageHandler.getMessagesArea();
        prescribedDrugs.setEditable(false);

        List<HashMap<String, String>> drugs = new ArrayList<>();

        addDrug.setOnAction(event -> {
            String drugNameText = drugName.getText();
            String amountText = amount.getText();

            if (drugNameText.isEmpty() || amountText.isEmpty()) {
                ShowAlert.error("You did not fill all required fields");
            } else {
                HashMap<String, String> drugEntry = new HashMap<>();
                drugEntry.put("drugName", drugNameText);
                drugEntry.put("amount", amountText);
                drugs.add(drugEntry);

                prescribedDrugs.appendText("Drug: " + drugNameText + ", Amount: " + amountText + "\n");
                drugName.clear();
                amount.clear();
            }
        });

        cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));

        sendRequestButton.setOnAction(event ->{
            ObjectMapper objectMapper = new ObjectMapper();

            // Wrap the DB response and additional metadata into a new JSON object
            ObjectNode jsonResponseNode = objectMapper.createObjectNode();
            jsonResponseNode.put("type", "addPrescription");
            jsonResponseNode.put("patient", pesel.getText());
            jsonResponseNode.put("doctor",this.getId());
            jsonResponseNode.set("drugs", objectMapper.valueToTree(drugs));
            clientHandler.sendMessage(jsonResponseNode.toString());
            stageHandler.setScene(new Scene(generateLayout(), 400, 300));
        });


        drugLayout.getChildren().addAll(peselLabel, pesel, drugLabel, drugName, drugAmmountLabel, amount, addDrug,prescribedDrugs, sendRequestButton,cancelButton);

        // Set the new scene
        Scene drugScene = new Scene(drugLayout, 400, 600);
        stageHandler.setScene(drugScene);
        stageHandler.displayMessage("");
    }
}
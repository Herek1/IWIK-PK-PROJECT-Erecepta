package com.example.clientservererecepta.Client;

import com.example.clientservererecepta.Client.Util.ShowAlert;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Admin extends User {
    private final ClientHandler clientHandler;
    private final StageHandler stageHandler;

    public Admin(int id, String name, String surname, ClientHandler clientHandler, StageHandler stageHandler) {
        super(id, name, surname);
        this.clientHandler = clientHandler;
        this.stageHandler = stageHandler;
    }

    @Override
    public VBox generateLayout() {
        Label welcomeLabel = new Label("Welcome, " + toString());


        Button addUser = new Button("Add user");
        addUser.setOnAction(event -> {
            addUserScene();
        });
        Button changePassword = new Button("Change Password");
        changePassword.setOnAction(event -> {
            openChangePasswordScene();
        });
        Button addDrug = new Button("Add drug");
        addDrug.setOnAction(event -> {
            addMedicineScene();
        });


        // Use the shared messagesArea from StageHandler
        TextArea messagesArea = stageHandler.getMessagesArea();
        stageHandler.displayMessage("");

        return new VBox(10, welcomeLabel, addUser, changePassword,addDrug,messagesArea);
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

    private void addUserScene() {
        VBox drugLayout = new VBox(10);
        Label peselLabel = new Label("Eneter user pesel");
        TextField pesel = new TextField();

        Label passwordLabel = new Label("Enter user password");
        TextField password = new TextField();

        Label nameLabel = new Label("Enter user name");
        TextField name = new TextField();

        Label surnameLabel = new Label("Enter user surname");
        TextField surname = new TextField();

        ComboBox<String> userTypeComboBox = new ComboBox<>();
        userTypeComboBox.getItems().addAll("doctor", "pharmacist", "patient", "admin"); // Add options to the dropdown
        userTypeComboBox.setPromptText("Choose a user type");


        Button sendRequestButton = new Button("Submit");
        Button cancelButton = new Button("Cancel");

        cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));

        sendRequestButton.setOnAction(event ->{
            if(pesel.getText().isEmpty() || password.getText().isEmpty() || name.getText().isEmpty() || surname.getText().isEmpty() || userTypeComboBox.getValue() == null){
                ShowAlert.error("You did not fill all required fields!");
            }else {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonRequestNode = objectMapper.createObjectNode();
                jsonRequestNode.put("type", "createUser");
                jsonRequestNode.put("pesel",pesel.getText());
                jsonRequestNode.put("password",password.getText());
                jsonRequestNode.put("name",name.getText());
                jsonRequestNode.put("surname",surname.getText());
                jsonRequestNode.put("usertype",userTypeComboBox.getValue());
                jsonRequestNode.put("id",getId());

                clientHandler.sendMessage(jsonRequestNode.toString());
                stageHandler.setScene(new Scene(generateLayout(), 400, 300));
            }
        });


        drugLayout.getChildren().addAll(peselLabel, pesel, passwordLabel, password, nameLabel, name, surnameLabel, surname, userTypeComboBox, sendRequestButton,cancelButton);

        // Set the new scene
        Scene drugScene = new Scene(drugLayout, 400, 600);
        stageHandler.setScene(drugScene);
        stageHandler.displayMessage("");
    }

    private void addMedicineScene(){
        VBox drugLayout = new VBox(10);
        Label drugNameLabel = new Label("Enter drug name");
        TextField drugName = new TextField();

        Label descriptionLabel = new Label("Enter description");
        TextField description = new TextField();

        Label priceLabel = new Label("Enter drug price");
        TextField price = new TextField();


        Button sendRequestButton = new Button("Submit");
        Button cancelButton = new Button("Cancel");

        cancelButton.setOnAction(event -> stageHandler.setScene(new Scene(generateLayout(), 400, 300)));

        sendRequestButton.setOnAction(event ->{
            if(drugName.getText().isEmpty() || description.getText().isEmpty() || price.getText().isEmpty()) {
                ShowAlert.error("You did not fill all required fields!");
            } else if (!isDouble(price.getText())) {
                ShowAlert.error("Price must be number");
            }else {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonRequestNode = objectMapper.createObjectNode();
                jsonRequestNode.put("type", "addDrugToDb");
                jsonRequestNode.put("drugName",drugName.getText());
                jsonRequestNode.put("description",description.getText());
                jsonRequestNode.put("price",price.getText());

                clientHandler.sendMessage(jsonRequestNode.toString());
                stageHandler.setScene(new Scene(generateLayout(), 400, 300));
            }
        });


        drugLayout.getChildren().addAll(drugNameLabel, drugName, descriptionLabel, description, priceLabel, price, sendRequestButton,cancelButton);

        // Set the new scene
        Scene drugScene = new Scene(drugLayout, 400, 600);
        stageHandler.setScene(drugScene);
        stageHandler.displayMessage("");
    }

    private boolean isDouble(String input){
        try{
            Double.parseDouble(input);
            return true;
        }catch (NumberFormatException e){}
        return false;
    }
}
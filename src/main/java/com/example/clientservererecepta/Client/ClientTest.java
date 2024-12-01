package com.example.clientservererecepta.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;


public class ClientTest extends Application {
    private static final int PORT = 12345;
    private StageHandler stageHandler;
    private PrintWriter out;

    @Override
    public void start(Stage stage) throws Exception {
        connectToServer(stage);
    }

    private void connectToServer(Stage stage) {
        try {
            Socket socket = new Socket("localhost", PORT);
            out = new PrintWriter(socket.getOutputStream(), true);

            ClientHandler clientHandler = new ClientHandler(out);
            this.stageHandler = new StageHandler(stage, clientHandler);
            Platform.runLater(stageHandler::setDefaultView);

            new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String message;
                    while ((message = in.readLine()) != null) {
                        handleServerResponse(message);
                    }
                } catch (IOException e) {
                    showError("Connection lost");
                }
            }).start();

        } catch (IOException e) {
            showError("Unable to connect to the server.");
        }
    }

    private void handleServerResponse(String message) {
        System.out.println("Received: " + message);
        //logic
        handleLogin(message);
        //do something
    }
    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.showAndWait();
        });
    }

    private void handleLogin(String message) {
        User user = null;

        // Parse the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(message);
        } catch (JsonProcessingException e) {
            showError("Invalid server response");
            return;
        }

        // Extract user role and other details
        String role = rootNode.get("role").asText();
        System.out.println("User role: " + role);

        switch (role) {
//            case "doctor":
//                user = new Doctor(rootNode.get("id").asInt(),
//                        rootNode.get("name").asText(),
//                        rootNode.get("surname").asText());
//                Platform.runLater(() -> stageHandler.switchToRoleView("Doctor"));
//                break;

//            case "pharmacist":
//                user = new Pharmacist(rootNode.get("id").asInt(),
//                        rootNode.get("name").asText(),
//                        rootNode.get("surname").asText());
//                Platform.runLater(() -> stageHandler.switchToRoleView("Pharmacist"));
//                break;

            case "Patient":
                user = new Patient(rootNode.get("id").asInt(),
                        rootNode.get("name").asText(),
                        rootNode.get("surname").asText());
                User finalUser = user;
                Platform.runLater(() -> stageHandler.switchToRoleView("Patient", finalUser));
                break;

            default:
                showError("Login failed. Invalid user role.");
                return;
        }

        // Use the user object as needed
        System.out.println("User logged in: " + user);
    }


}

package com.example.clientservererecepta.Client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ClientTest extends Application {
    private static final int PORT = 12345;
    private StageHandler stageHandler;
    private PrintWriter out;
    private ObjectMapper objectMapper; // JSON parser

    @Override
    public void start(Stage stage) {
        objectMapper = new ObjectMapper(); // Initialize the ObjectMapper
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
        try {
            JsonNode response = objectMapper.readTree(message); // Parse JSON response

            // Access the status from the first element in the data array
            JsonNode dataArray = response.get("data");
            if (dataArray == null || !dataArray.isArray() || dataArray.isEmpty()) {
                showError("Invalid server response: missing data array.");
                return;
            }

            JsonNode statusNode = dataArray.get(0).get("status");
            if (statusNode == null) {
                showError("Invalid server response: missing status.");
                return;
            }

            String status = statusNode.asText();
            if ("Error".equalsIgnoreCase(status)) {
                // Handle error case
                String userFriendlyError = dataArray.get(0).get("userFriendlyError").asText();
                showError(userFriendlyError);
                return;
            }

            if ("Success".equalsIgnoreCase(status)) {
                // Handle success case
                String type = response.get("type").asText();
                switch (type) {
                    case "login":
                        handleLoginSuccess(response);
                        break;
                    default:
                        showError("Unknown response type: " + type);
                        break;
                }
            } else {
                showError("Unexpected status: " + status);
            }
        } catch (Exception e) {
            showError("Invalid server response: " + message);
            e.printStackTrace();
        }
    }



    private void handleLoginSuccess(JsonNode response) {
        try {
            // Get the second object in the data array (index 1) for user details
            JsonNode userData = response.get("data").get(1);
            if (userData == null) {
                showError("Error: Missing user data in response.");
                return;
            }

            String userType = userData.get("userType").asText(); // Note the correct field name
            String userName = userData.get("name").asText();
            String userSurname = userData.get("surname").asText();
            int userId = userData.has("id") ? userData.get("id").asInt() : -1; // Handle missing 'id' gracefully

            final User user;
            switch (userType.toLowerCase()) { // Use case-insensitive comparison for safety
                case "doctor":
                    user = null; // Placeholder until Doctor class is implemented
                    break;
                case "pharmacist":
                    user = null; // Placeholder until Pharmacist class is implemented
                    break;
                case "patient":
                    user = new Patient(userId, userName, userSurname, stageHandler.getClientHandler());
                    break;
                default:
                    Platform.runLater(() -> stageHandler.displayMessage("Error: Unsupported role."));
                    return;
            }

            // Switch to the role-specific view on the JavaFX Application Thread
            Platform.runLater(() -> stageHandler.switchToRoleView(user));

        } catch (Exception e) {
            showError("Error processing login response.");
            e.printStackTrace();
        }
    }




    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

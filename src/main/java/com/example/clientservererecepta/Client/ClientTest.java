package com.example.clientservererecepta.Client;

import com.example.clientservererecepta.Client.Util.ShowAlert;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
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
                    ShowAlert.error("Connection lost");
                }
            }).start();

        } catch (IOException e) {
            ShowAlert.error("Unable to connect to the server.");
        }
    }

    private void handleServerResponse(String message) {
        System.out.println("Received: " + message);
        try {
            JsonNode response = objectMapper.readTree(message);
            JsonNode dataArray = response.get("data");
            if (dataArray == null || !dataArray.isArray() || dataArray.isEmpty()) {
                ShowAlert.error("Invalid server response: missing data array.");
                return;
            }

            JsonNode statusNode = dataArray.get(0).get("status");
            if (statusNode == null) {
                ShowAlert.error("Invalid server response: missing status.");
                return;
            }

            String status = statusNode.asText();
            if ("Error".equalsIgnoreCase(status)) {
                String userFriendlyError = dataArray.get(0).get("userFriendlyError").asText();
                ShowAlert.error(userFriendlyError);
                return;
            }

            if ("Success".equalsIgnoreCase(status)) {
                String type = response.get("type").asText();
                System.out.println("type: "+ type);
                switch (type) {
                    case "login":
                        handleLoginSuccess(response);
                        break;
                    case "getUserPrescriptions":
                        handlegetUserPrescriptionsSuccess(response);
                        break;
                    case "checkDrugAvailability":
                        handleCheckDrugAvailability(response);
                        break;
                    default:
                        ShowAlert.info("Action performed successfully.");
                        break;
                }
            } else {
                ShowAlert.error("Unexpected status: " + status);
            }
        } catch (Exception e) {
            ShowAlert.error("Invalid server response: " + message);
            e.printStackTrace();
        }
    }

    private void handleCheckDrugAvailability(JsonNode response) {
        System.out.println("handleCheckDrugAvailability\n" + response);
        User currentUser = UserSession.getCurrentUser();

        if (currentUser == null) {
            ShowAlert.error("No logged-in patient found.");
            return;
        }

        // Extract prescription data from the response
        JsonNode prescriptionsData = response.get("data").get(1); // Assuming prescriptions data is at index 1
        if (prescriptionsData == null || prescriptionsData.isEmpty()) {
            ShowAlert.error("Drug not found.");
            return;
        }

        if( currentUser instanceof Patient) {
            Patient currentPatient = (Patient) currentUser;
            // Process prescriptions and update the patient's view
            Platform.runLater(() -> {
                // Update the UI with the prescriptions
                currentPatient.updateDrugAvailability(response);
            });
        }
        if( currentUser instanceof Pharmacist) {
            Pharmacist currentPharmacist = (Pharmacist) currentUser;
            Platform.runLater(() -> {
                currentPharmacist.updateDrugAvailability(response);
            });
        }
    }


    private void handleLoginSuccess(JsonNode response) {
        try {
            JsonNode userData = response.get("data").get(1);
            if (userData == null) {
                ShowAlert.error("Error: Missing user data in response.");
                return;
            }

            String userType = userData.get("userType").asText();
            String userName = userData.get("name").asText();
            String userSurname = userData.get("surname").asText();
            int login = Integer.valueOf(userData.get("login").asText());

            final User user;
            switch (userType.toLowerCase()) {
                case "doctor":
                    user = new Doctor(login, userName, userSurname, stageHandler.getClientHandler(), stageHandler);
                    break;
                case "pharmacist":
                    user = new Pharmacist(login, userName, userSurname, stageHandler.getClientHandler(), stageHandler);
                    break;
                case "patient":
                    user = new Patient(login, userName, userSurname, stageHandler.getClientHandler(), stageHandler);
                    break;
                case "admin":
                    user = new Admin(login, userName, userSurname, stageHandler.getClientHandler(), stageHandler);
                    break;
                default:
                    Platform.runLater(() -> stageHandler.displayMessage("Error: Unsupported role."));
                    return;
            }

            UserSession.setCurrentUser(user);
            Platform.runLater(() -> stageHandler.switchToRoleView(user));

        } catch (Exception e) {
            ShowAlert.error("Error processing login response.");
            e.printStackTrace();
        }
    }


    private void handlegetUserPrescriptionsSuccess(JsonNode response) {
        User currentUser = UserSession.getCurrentUser();

        if (currentUser == null) {
            ShowAlert.error("No logged-in patient found.");
            return;
        }

        JsonNode prescriptionsData = response.get("data").get(1); // Assuming prescriptions data is at index 1
        if (prescriptionsData == null || prescriptionsData.isEmpty()) {
            ShowAlert.error("No prescriptions found.");
            return;
        }
        if(currentUser instanceof Patient) {
            Patient currentPatient = (Patient) currentUser;
            Platform.runLater(() -> {
                currentPatient.updatePrescriptions(response);
            });
        }
        if(currentUser instanceof Pharmacist) {
            Pharmacist currentPharmacist = (Pharmacist) currentUser;
            Platform.runLater(() -> {
                currentPharmacist.updatePrescriptions(response);
            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

//package com.example.clientservererecepta.Client;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.util.JSONPObject;
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//import java.io.*;
//import java.net.Socket;
//
//
//
//public class Client extends Application {
//    private static final int PORT = 12345;
//
//    private PrintWriter out;
//    private TextArea messagesArea;
//    private TextField login;
//    private TextField password;
//
//    @Override
//    public void start(Stage stage) {
//        VBox defaultLayout = generateDefaultLayout();
//        Scene scene = new Scene(defaultLayout, 400, 300);
//
//        stage.setTitle("Chat Client");
//        stage.setScene(scene);
//        stage.show();
//
//        connectToServer(stage);
//    }
//
//    private void connectToServer(Stage stage) {
//        try {
//            Socket socket = new Socket("localhost", PORT);
//            out = new PrintWriter(socket.getOutputStream(), true);
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
//                        String message;
//                        while ((message = in.readLine()) != null) {
//                            String finalMessage = message;
//                            System.out.println("try to log in as: " + finalMessage);
//                            createUserSession(finalMessage, stage);
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void createUserSession(String finalMessage, Stage stage) {
//        User user = null;
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode rootNode = null;
//        try {
//            rootNode = objectMapper.readTree(finalMessage);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//        String role = rootNode.get("role").asText();
//        System.out.println("test" + role);
//        switch (role) {
//            case "doctor":
//                break;
//            case "pharmacist":
//                break;
//            case "Patient":
//                user = new Patient(rootNode.get("id").asInt(),rootNode.get("name").asText(),rootNode.get("surname").asText());
//                break;
//            default:
//                messagesArea.appendText("failed to log in, try again\n");
//                System.out.println("Invalid user type: " + finalMessage);
//                return; // Exit if the type is unknown.
//        }
//
//        assert user != null; // check if user was generated
//        updateUserSession(stage, user.generateLayout());
//    }
//
//    private void updateUserSession(Stage stage, VBox vBox) {
//        //
//        Platform.runLater(() -> {
//            Scene sessionScene = new Scene(vBox, 400, 300);
//            stage.setScene(sessionScene); // Update the scene safely on the JavaFX Application Thread.
//        });
//    }
//
//    private VBox generateDefaultLayout(){
//        login = new TextField();
//        login.setPromptText("Login");
//
//        password = new TextField();
//        password.setPromptText("Password");
//
//        messagesArea = new TextArea();
//        messagesArea.setEditable(false);
//        Button sendButton = new Button("Login");
//
//        sendButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                sendMessage();
//            }
//        });
//
//        VBox layout = new VBox(10, messagesArea,login, password, sendButton);
//        return layout;
//    }
//
//
//    private void sendMessage() {
//        String loginData = login.getText() + ";" + password.getText();
//        if (loginData.length() > 1) {
//            out.println(loginData);
//        }
//    }
//
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
package com.example.clientservererecepta.Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;



public class Client extends Application {
    private static final int PORT = 12345;

    private PrintWriter out;
    private TextArea messagesArea;
    private TextField login;
    private TextField password;

    @Override
    public void start(Stage stage) {
        login = new TextField();
        login.setPromptText("Login");

        password = new TextField();
        password.setPromptText("Password");

        messagesArea = new TextArea();
        messagesArea.setEditable(false);
        Button sendButton = new Button("Login");

        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendMessage();
            }
        });

        VBox layout = new VBox(10, messagesArea,login, password, sendButton);
        Scene scene = new Scene(layout, 400, 300);

        stage.setTitle("Chat Client");
        stage.setScene(scene);
        stage.show();

        connectToServer(stage);
    }

    private void connectToServer(Stage stage) {
        try {
            Socket socket = new Socket("localhost", PORT);
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                        String message;
                        while ((message = in.readLine()) != null) {
                            String finalMessage = message;
                            System.out.println("try to log in as: " + finalMessage);
                            createUserSession(finalMessage, stage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createUserSession(String finalMessage, Stage stage) {
        User user;

        switch (finalMessage) {
            case "doctor":
                user = new Doctor("temp","temp");
                break;
            case "pharmacist":
                user = new Pharmacist("temp","temp");
                break;
            default:
                messagesArea.appendText("failed to log in, try again\n");
                System.out.println("Invalid user type: " + finalMessage);
                return; // Exit if the type is unknown.
        }

        prepareSession(user, stage);
    }

    private void prepareSession(User user, Stage stage) {
        VBox newLayout = user.generateLayout();
        //
        Platform.runLater(() -> {
            Scene sessionScene = new Scene(newLayout, 400, 300);
            stage.setScene(sessionScene); // Update the scene safely on the JavaFX Application Thread.
        });
    }

    private void sendMessage() {
        String loginData = login.getText() + ";" + password.getText();
        if (loginData.length() > 1) {
            out.println(loginData);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

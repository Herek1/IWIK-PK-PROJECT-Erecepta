package com.example.clientservererecepta.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
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


public class ClientTest extends Application {
    private static final int PORT = 12345;
    //private StageHandler stageHandler;
    private PrintWriter out;

    @Override
    public void start(Stage stage) throws Exception {
        connectToServer(stage);
    }

    private void connectToServer(Stage stage) {
        try {
            Socket socket = new Socket("localhost", PORT);
            out = new PrintWriter(socket.getOutputStream(), true);

            // Initialize the StageHandler and set the default view
            StageHandler stageHandler = new StageHandler(stage, out);
            Platform.runLater(stageHandler::setDefaultView);

            // Start a new thread for server communication
            new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String message;
                    while ((message = in.readLine()) != null) {
                        String finalMessage = message;
                        System.out.println("try to log in as: " + finalMessage);
                        // Handle server messages here
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

package com.example.clientservererecepta.Client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.PrintWriter;

public class StageHandler {
    Stage stage;
    PrintWriter out;
    private TextArea messagesArea;
    private TextField login;
    private TextField password;
    public StageHandler(Stage stage, PrintWriter out) {
        this.stage = stage;
        this.out = out;
    }
    public void setDefaultView(){
        VBox defaultLayout = generateDefaultLayout();
        Scene scene = new Scene(defaultLayout, 400, 300);
        stage.setTitle("Chat Client");
        stage.setScene(scene);
        stage.show();
    }

    private VBox generateDefaultLayout() {
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
        return layout;
    }
    public void sendMessage(){
        String loginData = login.getText() + ";" + password.getText();
        if (loginData.length() > 1) {
            out.println(loginData);
        }
    }

}

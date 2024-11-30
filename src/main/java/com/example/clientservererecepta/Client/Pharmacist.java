//package com.example.clientservererecepta.Client;
//
//import javafx.scene.control.Label;
//import javafx.scene.control.Button;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//public class Pharmacist extends User {
//    public Pharmacist(int id, String name, String surname) {
//        super(id, name, surname);
//    }
//
//    @Override
//    public VBox generateLayout(Stage stage) {
//        Label welcomeLabel = new Label("Welcome, Pharmacist " + toString());
//        Button managePatientsButton = new Button("Manage receipes");
//        Button logoutButton = new Button("Logout");
//
//        // Return a specific layout for the doctor.
//        return new VBox(10, welcomeLabel, managePatientsButton, logoutButton);
//    }
//}
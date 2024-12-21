package com.example.clientservererecepta.Server.Requests;

import com.example.clientservererecepta.DbEngine.Engine;
import com.example.clientservererecepta.DbEngine.dao.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestHandler {
    Connection newConnection;
    UsersDAO newUsersDAO;
    Engine myEngine;
    String response = "";
    public RequestHandler() {
        myEngine = new Engine();
        myEngine.start();
        newConnection = myEngine.returnConnection();
        newUsersDAO = new UsersDAO(newConnection);
    }
    public String handle(String request) throws IOException {
        String requestHeader = request.split(";")[1];
        switch (requestHeader){
            case "login":
                response = LoginHandler.handle(request, newUsersDAO);
                break;
            case "getPrescriptions":
                response = PrescriptionHandler.handle(request, newUsersDAO);
                break;
            case "checkDrugAvailability":
                response = DrugAvailabilityHandler.handle(request, newUsersDAO);
                break;
            case "changePassword":
                response = PasswordHandler.handle(request, newUsersDAO);
                break;
            case "addPrescription":
                response = null;
                break;
            default:
                response = "Invalid request";
                break;
        }
        System.out.println("response: " + response);
        return response;
    }
}

package com.example.clientservererecepta.Server.Requests;

import com.example.clientservererecepta.DbEngine.Engine;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;

public class RequestHandler {
    Connection newConnection;
    Engine myEngine;
    String response = "";
    public RequestHandler() {
        myEngine = new Engine();
        myEngine.start();
        newConnection = myEngine.returnConnection();
    }
    public String handle(String request) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode requestJson = objectMapper.readTree(request);

        // Extract the "type" field from JSON
        String type = requestJson.get("type").asText();
        System.out.println("Request type: " + type);
        switch (type){
            case "login":
                response = UserHandler.login(request, newConnection);
                break;
            case "createUser":
                response = UserHandler.addUser(request, newConnection);
                break;
            case "getPrescriptions":
                response = PrescriptionHandler.handle(request, newConnection);
                break;
            case "checkDrugAvailability":
                response = DrugAvailabilityHandler.checkDrug(request, newConnection);
                break;
            case "addDrugToDb":
                response = DrugAvailabilityHandler.addDrugToDb(request, newConnection);
                break; 
            case "changePassword":
                response = UserHandler.changePassword(request, newConnection);
                break;
            case "addPrescription":
                response = PrescriptionHandler.addPrescription(request, newConnection);
                break;
            default:
                response = "Invalid request";
                break;
        }
        System.out.println("response: " + response);
        return response;
    }
}

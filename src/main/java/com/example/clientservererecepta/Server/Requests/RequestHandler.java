package com.example.clientservererecepta.Server.Requests;

import com.example.clientservererecepta.DbEngine.DAO.UsersDAO;
import com.example.clientservererecepta.DbEngine.Engine;

import java.sql.Connection;

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
    public String handle(String request){
        String requestHeader = request.split(";")[0];
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

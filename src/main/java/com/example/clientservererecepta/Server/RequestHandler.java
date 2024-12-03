package com.example.clientservererecepta.Server;

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
                System.out.println("login");
                response = LoginHandler.handle(request, newUsersDAO);
                break;
            default:
                response = "Invalid request";
                break;
        }
        return response;
    }
}

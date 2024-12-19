package com.example.clientservererecepta.Server.Requests;

import com.example.clientservererecepta.DbEngine.dao.UsersDAO;
import com.example.clientservererecepta.Server.Util.ErrorResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.List;

public class LoginHandler {
    public static String handle(String request, UsersDAO usersDAO) {
        try {
            // Split the request string into parts
            String[] parts = request.split(";");
            String login = parts[1];
            String password = parts[2];

            // Query the database for the user
            System.out.println("Login: " + login);
            System.out.println("Password: " + password);
            List<HashMap<String, String>> dbResponse = usersDAO.getUser(login, password);

            // Create an ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Wrap the DB response and additional metadata into a new JSON object
            ObjectNode jsonResponseNode = objectMapper.createObjectNode();
            jsonResponseNode.put("type", "login"); // Add request type
            jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse)); // Add the DB response as 'data'

            // Convert the ObjectNode to a JSON string
            return objectMapper.writeValueAsString(jsonResponseNode);
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();

            return ErrorResponseUtil.createErrorResponse("An unexpected error occurred during login.");
        }
    }
}
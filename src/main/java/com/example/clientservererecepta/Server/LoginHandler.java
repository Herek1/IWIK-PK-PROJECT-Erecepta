package com.example.clientservererecepta.Server;

import com.example.clientservererecepta.DbEngine.DAO.UsersDAO;
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
            String jsonResponse = objectMapper.writeValueAsString(jsonResponseNode);
            String prettyPrint = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dbResponse);
            System.out.println("Login handler prepared: " + prettyPrint);
            return jsonResponse;
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();

            // Return error response
            HashMap<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An unexpected error occurred.");
            try {
                return new ObjectMapper().writeValueAsString(errorResponse);
            } catch (Exception jsonException) {
                jsonException.printStackTrace();
                return "{\"status\":\"error\",\"message\":\"Critical failure.\"}";
            }
        }
    }
}
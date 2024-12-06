package com.example.clientservererecepta.Server;

import com.example.clientservererecepta.DbEngine.DAO.UsersDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

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

            // Convert the response to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(dbResponse);
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
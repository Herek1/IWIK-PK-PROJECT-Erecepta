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
            List<HashMap<String, String>> dbResponse = usersDAO.getUser(login, password);

            // Prepare the response structure
            HashMap<String, Object> response = new HashMap<>();

            if (dbResponse.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Invalid login or password.");
            } else {
                response.put("status", "success");
                response.put("message", "Login successful.");
                response.put("data", dbResponse);
            }

            // Convert the response to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(response);
            System.out.println("Login handler prepared: " + jsonResponse);
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
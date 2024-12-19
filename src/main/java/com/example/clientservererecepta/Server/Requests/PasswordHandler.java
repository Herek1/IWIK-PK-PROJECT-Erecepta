package com.example.clientservererecepta.Server.Requests;

import com.example.clientservererecepta.DbEngine.dao.UsersDAO;
import com.example.clientservererecepta.Server.Util.ErrorResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PasswordHandler extends RequestHandler {
    public static String handle(String request, UsersDAO usersDAO) {
        try {
            // Split the request string into parts
            String[] parts = request.split(";");
            String newPassword = parts[1];
            String login = parts[2];
            List<HashMap<String, String>> dbResponse = new ArrayList<>();

            dbResponse = changePasswordTest();

            // Create an ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Wrap the DB response and additional metadata into a new JSON object
            ObjectNode jsonResponseNode = objectMapper.createObjectNode();
            jsonResponseNode.put("type", "changePassword"); // Add request type
            jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse)); // Add the DB response as 'data'

            // Convert the ObjectNode to a JSON string
            return objectMapper.writeValueAsString(jsonResponseNode);
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
            // Return error response
            return ErrorResponseUtil.createErrorResponse("An unexpected error occurred while fetching prescriptions.");
        }
    }

    public static List<HashMap<String, String>> changePasswordTest() {
        List<HashMap<String, String>> drugAvailabilityList = new ArrayList<>();

        // Default error message (simulating a typical DB response structure)
        HashMap<String, String> defaultErrorMessage = new HashMap<>();
        defaultErrorMessage.put("status", "Success");
        defaultErrorMessage.put("exception", "None");
        defaultErrorMessage.put("userFriendlyError", "No errors detected.");
        defaultErrorMessage.put("errorMessage", "No issues.");
        drugAvailabilityList.add(defaultErrorMessage);

        return drugAvailabilityList;
    }
}

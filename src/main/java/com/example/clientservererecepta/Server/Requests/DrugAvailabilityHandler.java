package com.example.clientservererecepta.Server.Requests;

import com.example.clientservererecepta.DbEngine.DAO.UsersDAO;
import com.example.clientservererecepta.Server.Util.ErrorResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DrugAvailabilityHandler {
        public static String handle(String request, UsersDAO usersDAO) {
            try {
                // Split the request string into parts
                String[] parts = request.split(";");
                String drugName = parts[1];
                List<HashMap<String, String>> dbResponse = new ArrayList<>();
                Boolean checkLocation = false;
                if(parts.length>2){
                    checkLocation = true;
                    String location = parts[2];
                }

                dbResponse = generateTestDrugAvailability();
                if(checkLocation) {
                    dbResponse = generateTestDrugAvailability();
                }

                // Create an ObjectMapper
                ObjectMapper objectMapper = new ObjectMapper();

                // Wrap the DB response and additional metadata into a new JSON object
                ObjectNode jsonResponseNode = objectMapper.createObjectNode();
                jsonResponseNode.put("type", "checkDrugAvailability"); // Add request type
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

    public static List<HashMap<String, String>> generateTestDrugAvailability() {
        List<HashMap<String, String>> drugAvailabilityList = new ArrayList<>();

        // Default error message (simulating a typical DB response structure)
        HashMap<String, String> defaultErrorMessage = new HashMap<>();
        defaultErrorMessage.put("status", "Success");
        defaultErrorMessage.put("exception", "None");
        defaultErrorMessage.put("userFriendlyError", "No errors detected.");
        defaultErrorMessage.put("errorMessage", "No issues.");
        drugAvailabilityList.add(defaultErrorMessage);

        // Example drug availability data
        HashMap<String, String> drug1 = new HashMap<>();
        drug1.put("drugName", "Aspirin");
        drug1.put("address", "Pharmacy Alpha, 10 Main Street");
        drug1.put("amount", "20");
        drugAvailabilityList.add(drug1);

        HashMap<String, String> drug2 = new HashMap<>();
        drug2.put("drugName", "Amoxicillin");
        drug2.put("address", "Pharmacy Beta, 25 Broadway");
        drug2.put("amount", "35");
        drugAvailabilityList.add(drug2);

        HashMap<String, String> drug3 = new HashMap<>();
        drug3.put("drugName", "Metformin");
        drug3.put("address", "Pharmacy Gamma, 50 High Street");
        drug3.put("amount", "15");
        drugAvailabilityList.add(drug3);

        return drugAvailabilityList;
    }
    }

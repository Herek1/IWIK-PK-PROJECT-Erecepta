package com.example.clientservererecepta.Server.Requests;

import com.example.clientservererecepta.DbEngine.dao.UsersDAO;
import com.example.clientservererecepta.Server.Util.ErrorResponseUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PrescriptionHandler {
    public static String handle(String request, Connection connection) {
        UsersDAO usersDAO = new UsersDAO(connection);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request);
            String id = root.get("id").asText();

            List<HashMap<String, String>> dbResponse = getPrescription(id, usersDAO);
            ObjectMapper objectMapper = new ObjectMapper();

            ObjectNode jsonResponseNode = objectMapper.createObjectNode();
            jsonResponseNode.put("type", "getUserPrescriptions"); // Add request type
            jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse)); // Add the DB response as 'data'

            return objectMapper.writeValueAsString(jsonResponseNode);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse("An unexpected error occurred while fetching prescriptions.");
        }
    }

    public static String addPrescription(String request, Connection connection) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode response = objectMapper.readTree(request);
        System.out.println(response.toPrettyString());
        return "";
    }

    public static List<HashMap<String, String>> getPrescription(String id, UsersDAO usersDAO) {
        List<HashMap<String, String>> prescriptions = new ArrayList<>();

        // Create the first prescription data (HashMap)
        HashMap<String, String> prescriptionData = new HashMap<>();
        prescriptionData.put("exception", "There is no exception");
        prescriptionData.put("errorMessage", "There is no error message");
        prescriptionData.put("userFriendlyError", "There is no error");
        prescriptionData.put("status", "Success");
        prescriptionData.put("code", "34567");
        prescriptionData.put("date", "01/12/2024");
        prescriptionData.put("patient", "John Doe");

        StringBuilder drugs = new StringBuilder();
        drugs.append("ID: 101, Name: Paracetamol, Description: Pain reliever and fever reducer, Price: 9.99\n");
        drugs.append("ID: 102, Name: Ibuprofen, Description: Anti-inflammatory drug, Price: 15.49\n");
        drugs.append("ID: 103, Name: Amoxicillin, Description: Antibiotic for bacterial infections, Price: 29.99");
        prescriptionData.put("drugs", drugs.toString());

        prescriptions.add(prescriptionData);

        // You can add more prescriptions in the list if required
        // For example, another prescription:
        HashMap<String, String> prescriptionData2 = new HashMap<>();
        prescriptionData2.put("exception", "No exception");
        prescriptionData2.put("errorMessage", "No error");
        prescriptionData2.put("userFriendlyError", "No issues");
        prescriptionData2.put("status", "Success");
        prescriptionData2.put("code", "12345");
        prescriptionData2.put("date", "02/12/2024");
        prescriptionData2.put("patient", "Jane Doe");

        StringBuilder drugs2 = new StringBuilder();
        drugs2.append("ID: 201, Name: Cough Syrup, Description: Cough suppressant, Price: 4.99\n");
        drugs2.append("ID: 202, Name: Antihistamine, Description: Allergy medication, Price: 8.49");
        prescriptionData2.put("drugs", drugs2.toString());

        prescriptions.add(prescriptionData2);

        return prescriptions;
    }
}

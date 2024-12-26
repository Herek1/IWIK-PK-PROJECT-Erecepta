package com.example.clientservererecepta.Server.Requests;

import com.example.clientservererecepta.DbEngine.dao.AvailabilityDrugDAO;
import com.example.clientservererecepta.DbEngine.dao.MedicinesDAO;
import com.example.clientservererecepta.Server.Util.ErrorResponseUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public class DrugAvailabilityHandler {
        public static String checkDrug(String request, Connection connection) {
            AvailabilityDrugDAO dao = new AvailabilityDrugDAO(connection);
            try {

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(request);
                String drugName = root.get("drugName").asText();

                List<HashMap<String, String>> dbResponse = dao.getAvailabilityByMedicineName(drugName);

                ObjectNode jsonResponseNode = objectMapper.createObjectNode();
                jsonResponseNode.put("type", "checkDrugAvailability"); // Add request type
                jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse)); // Add the DB response as 'data'

                return objectMapper.writeValueAsString(jsonResponseNode);
            } catch (Exception e) {
                e.printStackTrace();
                return ErrorResponseUtil.createErrorResponse("An unexpected error occurred while fetching prescriptions.");
            }
        }
        public static String addDrugToDb(String request, Connection connection) {
            MedicinesDAO dao = new MedicinesDAO(connection);
            try {

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(request);
                String drugName = root.get("drugName").asText();
                String description = root.get("description").asText();
                String price = root.get("price").asText();

                List<HashMap<String, String>> dbResponse = dao.addMedicine(drugName,description, Double.parseDouble(price));

                ObjectNode jsonResponseNode = objectMapper.createObjectNode();
                jsonResponseNode.put("type", "addDrugToDb"); // Add request type
                jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse)); // Add the DB response as 'data'

                return objectMapper.writeValueAsString(jsonResponseNode);
            } catch (Exception e) {
                e.printStackTrace();
                return ErrorResponseUtil.createErrorResponse("An unexpected error occurred while fetching prescriptions.");
            }

        }
    }

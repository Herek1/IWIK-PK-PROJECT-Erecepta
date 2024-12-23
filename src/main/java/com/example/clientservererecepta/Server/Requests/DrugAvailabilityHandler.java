package com.example.clientservererecepta.Server.Requests;

import com.example.clientservererecepta.DbEngine.dao.AvailabilityDrugDAO;
import com.example.clientservererecepta.DbEngine.dao.UsersDAO;
import com.example.clientservererecepta.Server.Util.ErrorResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DrugAvailabilityHandler {
        public static String handle(String request, AvailabilityDrugDAO dao) {
            try {
                String[] parts = request.split(";");
                String drugName = parts[2];
                List<HashMap<String, String>> dbResponse = new ArrayList<>();
                String location = "";
                if(parts.length>3){
                    location = parts[3];
                }

                dbResponse = dao.getAvailabilityByMedicineName(drugName);
                ObjectMapper objectMapper = new ObjectMapper();


                ObjectNode jsonResponseNode = objectMapper.createObjectNode();
                jsonResponseNode.put("type", "checkDrugAvailability"); // Add request type
                jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse)); // Add the DB response as 'data'

                return objectMapper.writeValueAsString(jsonResponseNode);
            } catch (Exception e) {
                e.printStackTrace();
                return ErrorResponseUtil.createErrorResponse("An unexpected error occurred while fetching prescriptions.");
            }
        }
    }

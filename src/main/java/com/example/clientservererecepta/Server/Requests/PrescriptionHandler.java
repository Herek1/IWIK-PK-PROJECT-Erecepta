package com.example.clientservererecepta.Server.Requests;

import com.example.clientservererecepta.DbEngine.dao.DrugListDAO;
import com.example.clientservererecepta.DbEngine.dao.RecipeDAO;
import com.example.clientservererecepta.Server.Util.ErrorResponseUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Connection;
import java.util.*;

public class PrescriptionHandler {
    public static String checkPrescriptionsById(String request, Connection connection) {
        DrugListDAO drugListDAO = new DrugListDAO(connection);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request);
            String recipeId = root.get("id").asText();

            List<HashMap<String, String>> dbResponse = drugListDAO.getDrugsByRecipeId(recipeId);
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

    public static String checkPrescriptionsByUser(String request, Connection connection) {
        DrugListDAO drugListDAO = new DrugListDAO(connection);
        RecipeDAO recipeDAO = new RecipeDAO(connection);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(request);
            String userId = root.get("id").asText();

            // Fetch all recipes for the user
            List<HashMap<String, String>> dbResponse = recipeDAO.getRecipes(userId, "patient");

            // Create the JSON response object
            ObjectNode jsonResponseNode = objectMapper.createObjectNode();
            jsonResponseNode.put("type", "getUserPrescriptions"); // Add request type

            // Create the "data" array
            ArrayNode dataArray = objectMapper.createArrayNode();

            // Add the first item from dbResponse as the first item in the "data" array
            ObjectNode dataNode = objectMapper.valueToTree(dbResponse.get(0));
            dataArray.add(dataNode);

            // Create a container for recipe data
            ObjectNode recipesNode = objectMapper.createObjectNode();

            // Iterate over recipes (starting from the second one)
            for (int i = 1; i < dbResponse.size(); i++) {
                HashMap<String, String> recipe = dbResponse.get(i);
                String recipeId = recipe.get("recipeId");

                // Fetch drugs for this recipe
                List<HashMap<String, String>> drugs = drugListDAO.getDrugsByRecipeId(recipeId);

                // Convert recipe data and drugs to JSON
                ObjectNode recipeNode = objectMapper.createObjectNode();
                recipeNode.put("date", recipe.get("date"));
                recipeNode.put("doctorName", recipe.get("doctorName"));
                recipeNode.put("doctorSurname", recipe.get("doctorSurname"));
                recipeNode.put("recipeId", recipeId);

                // Add drugs list to the recipe node
                recipeNode.set("drugs", objectMapper.valueToTree(drugs));

                // Add this recipe to the recipes container
                recipesNode.set("recipe" + recipeId, recipeNode);

                // Add the recipe to the "data" array
                dataArray.add(recipeNode);
            }

            // Add the "data" array to the response
            jsonResponseNode.set("data", dataArray);

            // Convert the final JSON response to string
            return objectMapper.writeValueAsString(jsonResponseNode);
        }catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse("An unexpected error occurred while fetching prescriptions.");
        }
    }

    public static String addPrescription(String request, Connection connection) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode response = objectMapper.readTree(request);
        RecipeDAO recipeDAO = new RecipeDAO(connection);
        String doctorId = response.get("doctor").asText();
        String patientId = response.get("patient").asText();
        java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        List<HashMap<String, String>> dbResponse = recipeDAO.insertRecipe(doctorId, patientId, date);

        HashMap<String, String> responseMap = dbResponse.get(0); // Get the first HashMap
        String recipeId = responseMap.get("recipeId");

        DrugListDAO drugListDAO = new DrugListDAO(connection);
        JsonNode drugsArray = response.get("drugs");

        for (JsonNode drug : drugsArray) {
            String drugName = drug.get("drugName").asText(); // Get drug name
            int amount = Integer.parseInt(drug.get("amount").asText());     // Get amount
            dbResponse = drugListDAO.insertDrugToListLite(recipeId,drugName,amount,patientId);
        }


        ObjectNode jsonResponseNode = objectMapper.createObjectNode();
        jsonResponseNode.put("type", "addDrugsToDb"); // Add request type
        jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse)); // Add the DB response as 'data'
        return objectMapper.writeValueAsString(jsonResponseNode);
    }

    public static String sellDrug(String request, Connection connection) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode response = objectMapper.readTree(request);
        DrugListDAO drugListDAO = new DrugListDAO(connection);
        String drugName = response.get("drugName").asText();
        String recipeId = response.get("recipeId").asText();
        String pharmacistId = response.get("pharmacistId").asText();
        System.out.println(drugName+ " "+recipeId+ " "+pharmacistId);

        List<HashMap<String, String>> dbResponse = drugListDAO.updateDrugList(recipeId,drugName,pharmacistId, "Sold");
        ObjectNode jsonResponseNode = objectMapper.createObjectNode();
        jsonResponseNode.put("type", "soldDrug"); // Add request type
        jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse)); // Add the DB response as 'data'
        return objectMapper.writeValueAsString(jsonResponseNode);
    }
}

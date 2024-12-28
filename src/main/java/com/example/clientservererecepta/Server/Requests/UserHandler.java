package com.example.clientservererecepta.Server.Requests;

import com.example.clientservererecepta.DbEngine.dao.UsersDAO;
import com.example.clientservererecepta.Server.Util.ErrorResponseUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public class UserHandler {
    public static String login(String request, Connection connection) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request);

            String login = root.get("login").asText();
            String password = root.get("password").asText();

            UsersDAO usersDAO = new UsersDAO(connection);
            List<HashMap<String, String>> dbResponse = usersDAO.getUser(login, password);

            ObjectMapper objectMapper = new ObjectMapper();

            ObjectNode jsonResponseNode = objectMapper.createObjectNode();
            jsonResponseNode.put("type", "login");
            jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse));

            return objectMapper.writeValueAsString(jsonResponseNode);
        } catch (Exception e) {
            e.printStackTrace();

            return ErrorResponseUtil.createErrorResponse("An unexpected error occurred during login.");
        }
    }

    public static String changePassword(String request, Connection connection) {
        UsersDAO usersDAO = new UsersDAO(connection);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request);
            String login = root.get("id").asText();
            String newPassword = root.get("password").asText();
            List<HashMap<String, String>> dbResponse = usersDAO.updateUserPassword(login, newPassword);


            ObjectMapper objectMapper = new ObjectMapper();

            ObjectNode jsonResponseNode = objectMapper.createObjectNode();
            jsonResponseNode.put("type", "changePassword"); // Add request type
            jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse)); // Add the DB response as 'data'
            return objectMapper.writeValueAsString(jsonResponseNode);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse("An unexpected error occurred while fetching prescriptions.");
        }
    }

    public static String addUser(String request, Connection connection) {
        UsersDAO usersDAO = new UsersDAO(connection);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(request);
            String pesel = root.get("pesel").asText();
            String password = root.get("password").asText();
            String usertype = root.get("usertype").asText();
            String name = root.get("name").asText();
            String surname = root.get("surname").asText();


            List<HashMap<String, String>> dbResponse = usersDAO.createUser(pesel, password, usertype, name, surname);

            ObjectNode jsonResponseNode = objectMapper.createObjectNode();
            jsonResponseNode.put("type", "changePassword"); // Add request type
            jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse)); // Add the DB response as 'data'
            return objectMapper.writeValueAsString(jsonResponseNode);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse("An unexpected error occurred while fetching prescriptions.");
        }
    }
}
package com.example.clientservererecepta.Server.Requests;

import com.example.clientservererecepta.DbEngine.dao.UsersDAO;
import com.example.clientservererecepta.Server.Util.ErrorResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.List;

public class LoginHandler {
    public static String handle(String request, UsersDAO usersDAO) {
        try {
            String[] parts = request.split(";");
            String login = parts[2];
            String password = parts[3];

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
}
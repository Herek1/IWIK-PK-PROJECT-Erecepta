package com.example.clientservererecepta.Client;

import com.example.clientservererecepta.DbEngine.Engine;
import com.example.clientservererecepta.DbEngine.dao.RecipeDAO;
import com.example.clientservererecepta.DbEngine.dao.UsersDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public class Test {
    public static void main(String[] args) throws JsonProcessingException {
        Connection newConnection;
        Engine myEngine;
        myEngine = new Engine();
        myEngine.start();
        newConnection = myEngine.returnConnection();

        RecipeDAO newDao = new RecipeDAO(newConnection);
        List<HashMap<String, String>> dbResponse = newDao.getRecipes("123","patient");
        ObjectMapper objectMapper = new ObjectMapper();


        ObjectNode jsonResponseNode = objectMapper.createObjectNode();
        jsonResponseNode.put("type", "checkPrescription");
        jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse));

        System.out.println(jsonResponseNode.toPrettyString());



    }
}

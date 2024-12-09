package com.example.clientservererecepta.Client;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Test {
    public static void main(String[] args) throws JsonProcessingException {
        String request = "getDrug;test";
        String[] parts = request.split(";");
        String drugName = parts[1];
        if(parts.length>2){
            System.out.println(parts[2]);
        }
    }
}

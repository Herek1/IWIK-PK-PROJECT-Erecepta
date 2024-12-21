package com.example.clientservererecepta.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final Object lock = new Object(); // Lock for thread safety

    public void saveLogs(String request, String response) throws IOException {
        String log = createLog(request, response);

        // Get current date for the file name
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String fileName = formatter.format(date) + ".txt";

        synchronized (lock) { // Synchronize writing to the log file
            File file = new File(fileName);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(log);
                writer.newLine(); // Add a new line for each log
            } catch (IOException e) {
                throw new IOException("Error while saving log: " + e.getMessage(), e);
            }
        }
    }

    private String createLog(String request, String response) throws JsonProcessingException {
        String clientPort = request.split(";")[0];
        String requestType = request.split(";")[1];
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode data = objectMapper.readTree(response);
        JsonNode dataArray = data.get("data");
        String userFriendlyError = dataArray.get(0).get("userFriendlyError").asText();

        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        return (timestamp + ";" + clientPort + ";" + requestType + ";" + userFriendlyError);


    }
}

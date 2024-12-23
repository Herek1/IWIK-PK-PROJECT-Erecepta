package com.example.clientservererecepta.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final Object lock = new Object();
    private static final String LOG_DIRECTORY = "./Logs";

    public void saveLogs(String request, String response) throws IOException {
        String log = createLog(request, response);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String fileName = LOG_DIRECTORY + "/" + formatter.format(date) + ".txt";

        synchronized (lock) { // Synchronize writing to the log file
            File logDirectory = new File(LOG_DIRECTORY);

            // Ensure the directory exists
            if (!logDirectory.exists()) {
                if (!logDirectory.mkdir()) {
                    throw new IOException("Failed to create log directory: " + LOG_DIRECTORY);
                }
            }

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

        // Validate 'data' node exists and is an array
        JsonNode dataArray = data.get("data");
        if (dataArray == null || !dataArray.isArray()) {
            throw new IllegalArgumentException("Invalid 'data' array in response");
        }

        // Find the first element with 'userFriendlyError'
        String userFriendlyError = "No userFriendlyError found";
        for (JsonNode element : dataArray) {
            if (element.has("userFriendlyError")) {
                userFriendlyError = element.get("userFriendlyError").asText();
                break; // Exit loop after finding the first valid element
            }
        }

        if (userFriendlyError.equals("No userFriendlyError found")) {
            throw new IllegalArgumentException("'userFriendlyError' is missing in all elements");
        }

        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        return timestamp + ";" + clientPort + ";" + requestType + ";" + userFriendlyError;
    }

}

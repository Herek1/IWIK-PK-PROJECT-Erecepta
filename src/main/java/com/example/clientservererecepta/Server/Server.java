package com.example.clientservererecepta.Server;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class Server {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received from client: " + inputLine);
                    String userType = checkUser(inputLine);
                    out.println(userType);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String checkUser(String inputLine) {
            //DB lookup
            HashMap<String, String> testList = new HashMap<String, String>();
            String testUser ="{ \"id\": 1, \"name\": \"John\", \"surname\": \"Doe\", \"role\": \"Patient\" }";
            testList.put("test;admin", testUser);
            return testList.get(inputLine);
        }
    }
}
    
package com.example.clientservererecepta.Client;

import java.io.PrintWriter;

public class ClientHandler {
    private PrintWriter out;

    public ClientHandler(PrintWriter out) {
        this.out = out;
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
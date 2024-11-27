package com.example.clientservererecepta.Client;

import javafx.scene.layout.VBox;

public abstract class User {
    private static int id;
    private static String name;
    private static String surname;

    public User(int id, String name, String surname) {
        User.id = id;
        User.name = name;
        User.surname = surname;
    }

    public static String getSurname() {
        return surname;
    }

    public static String getName() {
        return name;
    }

    public abstract VBox generateLayout();

    @Override
    public String toString() {
        return name + " " + surname;
    }

}

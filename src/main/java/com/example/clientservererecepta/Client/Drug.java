package com.example.clientservererecepta.Client;

import java.math.BigDecimal;

public class Drug {
    private final int id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    public Drug (int id, String name, String description, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @Override
    public String toString() {
        String template = """
                %s
                %s
                %.2f
                """;
        return String.format(template, name, description, price);
    }
}

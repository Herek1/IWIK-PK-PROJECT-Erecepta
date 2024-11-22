package com.example.clientservererecepta;

import java.math.BigDecimal;

public class Drug {
    private final String name;
    private final String description;
    private final BigDecimal price;
    public Drug (String name, String description, BigDecimal price) {
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

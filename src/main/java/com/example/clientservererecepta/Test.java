package com.example.clientservererecepta;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        Drug ibuprofen = new Drug("ibuprofen","bol glowy", new BigDecimal("12.15"));
        Drug lek2 = new Drug("test","asd glowy", new BigDecimal("15.99"));
        Drug lek3 = new Drug("test2","asd jwg", new BigDecimal("99.99"));
        Pharmacist pharmacist = new Pharmacist("Kacper","Tokarz");
        List<Drug> drugs = new ArrayList<>();
        drugs.add(ibuprofen);
        drugs.add(lek2);
        drugs.add(lek3);
        Recipe test = new Recipe(pharmacist,drugs);
        System.out.println(test.toString());
    }
}

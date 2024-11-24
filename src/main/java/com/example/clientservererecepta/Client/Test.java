package com.example.clientservererecepta.Client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        Drug ibuprofen = new Drug(1,"ibuprofen","bol glowy", new BigDecimal("12.15"));
        Drug lek2 = new Drug(2,"test","asd glowy", new BigDecimal("15.99"));
        Drug lek3 = new Drug(3,"test2","asd jwg", new BigDecimal("99.99"));
        Pharmacist pharmacist = new Pharmacist("Kacper","Tokarz");
        List<Drug> drugs = new ArrayList<>();
        drugs.add(ibuprofen);
        drugs.add(lek2);
        drugs.add(lek3);
        Prescription test = new Prescription(pharmacist);
        test.addDrug(ibuprofen);
        System.out.println(test.toString());
    }
}

package com.example.clientservererecepta.Client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Prescription {
    private final int code;
    private final String date;
    private final String patient;
    private List<Drug> drugList;

    private final int lastId = 34567;

    public Prescription(User ignoreidUser) {
        this.code = lastId;
        this.date = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        this.patient = User.getName() + " " + User.getSurname();
        this.drugList = new ArrayList<>();
    }

    public void addDrug(Drug drug){
        drugList.add(drug);
    }
    @Override
    public String toString() {
        String template = """
                Code: %d   Date: %s
                Patient: %s
                Drugs: %s
                """;
        StringBuilder formatedDrugs = new StringBuilder();
        for (Drug temp : drugList){
            formatedDrugs.append("\n");
            formatedDrugs.append(temp.toString());
        }
        return String.format(template,code,date,patient,formatedDrugs);
    }

}

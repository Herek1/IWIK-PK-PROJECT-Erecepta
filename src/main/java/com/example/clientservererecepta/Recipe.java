package com.example.clientservererecepta;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Recipe {
    private final int code;
    private final String date;
    private final String patient;
    private final List<Drug> drugList;

    private final int lastId = 34567;

    public Recipe(User ignoreidUser, List<Drug> drugList) {
        this.code = lastId;
        this.date = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        this.patient = User.getName() + " " + User.getSurname();
        this.drugList = drugList;
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

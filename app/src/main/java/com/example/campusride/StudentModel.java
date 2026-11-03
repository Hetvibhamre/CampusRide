package com.example.campusride;

public class StudentModel {

    String name, programme, branch, year, sapId, validity;

    public StudentModel(String name, String programme, String branch, String year, String sapId, String validity) {
        this.name = name;
        this.programme = programme;
        this.branch = branch;
        this.year = year;
        this.sapId = sapId;
        this.validity = validity;
    }

    public String getName() {
        return name;
    }

    public String getProgramme() {
        return programme;
    }

    public String getBranch() {
        return branch;
    }

    public String getYear() {
        return year;
    }

    public String getSapId() {
        return sapId;
    }

    public String getValidity() {
        return validity;
    }
}
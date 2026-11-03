package com.example.campusride;

public class ScheduleModel {

    int id;
    String label, time, type;
    public ScheduleModel() {
    }

    public ScheduleModel(int id, String label, String time, String type) {
        this.id = id;
        this.label = label;
        this.time = time;
        this.type = type;
    }

    public int getId() { return id; }
    public String getLabel() { return label; }
    public String getTime() { return time; }
    public String getType() { return type; }
    public void setId(int id) { this.id = id; }
    public void setLabel(String label) { this.label = label; }
    public void setTime(String time) { this.time = time; }
    public void setType(String type) { this.type = type; }
}
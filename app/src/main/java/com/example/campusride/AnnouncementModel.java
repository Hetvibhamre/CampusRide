package com.example.campusride;

public class AnnouncementModel {

    private String text;
    private long timestamp;
    private String key; // 🔥 Firebase key

    // 🔥 REQUIRED for Firebase
    public AnnouncementModel() {}

    public AnnouncementModel(String text, long timestamp) {
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
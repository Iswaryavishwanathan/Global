package com.example.global.DAO;

import java.time.LocalDateTime;

public class CommonDAO {

    private LocalDateTime dateTime;
    private String tagName;
    private Double val;

    // Constructor
    public CommonDAO(LocalDateTime dateTime, String tagName, Double val) {
        this.dateTime = dateTime;
        this.tagName = tagName;
        this.val = val;
    }

    // Getters and Setters
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Double getVal() {
        return val;
    }

    public void setVal(Double val) {
        this.val = val;
    }
}

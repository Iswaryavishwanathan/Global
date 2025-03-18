package com.example.global.DAO.SecondaryDAO;

import java.time.LocalDateTime;

public class SecondaryDAO {
    private LocalDateTime dateAndTime;
    private int millitm;
    private Integer tagIndex;
    private String tagName; 
    private double val;
    private String status;
    private String marker;
    private String databaseSource;

    public SecondaryDAO(LocalDateTime dateAndTime, int millitm, Integer tagIndex, String tagName, double val, String status, String marker, String databaseSource) {
        this.dateAndTime = dateAndTime;
        this.millitm = millitm;
        this.tagIndex = tagIndex;
        this.tagName = tagName;
        this.val = val;
        this.status = status;
        this.marker = marker;
        this.databaseSource = databaseSource;
    }

    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName;}

    // Getters and Setters
    public LocalDateTime getDateAndTime() { return dateAndTime; }
    public void setDateAndTime(LocalDateTime dateAndTime) { this.dateAndTime = dateAndTime; }

    public int getMillitm() { return millitm; }
    public void setMillitm(int millitm) { this.millitm = millitm; }

    public Integer getTagIndex() { return tagIndex; }
    public void setTagIndex(Integer tagIndex) { this.tagIndex = tagIndex; }

    public double getVal() { return val; }
    public void setVal(double val) { this.val = val; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMarker() { return marker; }
    public void setMarker(String marker) { this.marker = marker; }

    public String getDatabaseSource() { return databaseSource; }
    public void setDatabaseSource(String databaseSource) { this.databaseSource = databaseSource; }
}
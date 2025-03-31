package com.example.global.DAO;

import java.time.LocalDateTime;

public class KWhConsumptionDAO {
    private String databaseName;
    private String tagName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private double startValue;
    private double endValue;
    private double consumption;
    

    // ✅ Updated constructor to include startDateTime and endDateTime
    public KWhConsumptionDAO(String databaseName, String tagName, LocalDateTime startDateTime, LocalDateTime endDateTime, 
                             Double startValue, Double endValue) {
        this.databaseName = databaseName;
        this.tagName = tagName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.startValue = (startValue != null) ? startValue : 0.0;
        this.endValue = (endValue != null) ? endValue : 0.0;
        this.consumption = this.endValue - this.startValue;
      
    }
    public String getDatabaseName() { return databaseName; }
    public String getTagName() { return tagName; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public LocalDateTime getEndDateTime() { return endDateTime; }
    public double getStartValue() { return startValue; }
    public double getEndValue() { return endValue; }
    public double getConsumption() { return consumption; }

}

package com.example.global.DAO;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class KWhConsumptionDAO {
    private String databaseName;
    private String tagName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private double startValue;
    private double endValue;
    private double consumption;

    // Constructor for native query (Timestamp)
    public KWhConsumptionDAO(String databaseName, String tagName, Timestamp startDateTime, Timestamp endDateTime, 
                             Number startValue, Number endValue) {
        this.databaseName = databaseName;
        this.tagName = tagName;
        this.startDateTime = (startDateTime != null) ? startDateTime.toLocalDateTime() : null;
        this.endDateTime = (endDateTime != null) ? endDateTime.toLocalDateTime() : null;
        this.startValue = (startValue != null) ? startValue.doubleValue() : 0.0;
        this.endValue = (endValue != null) ? endValue.doubleValue() : 0.0;
        this.consumption = this.endValue - this.startValue;
    }


    // Getters
    public String getDatabaseName() { return databaseName; }
    public String getTagName() { return tagName; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public LocalDateTime getEndDateTime() { return endDateTime; }
    public double getStartValue() { return startValue; }
    public double getEndValue() { return endValue; }
    public double getConsumption() { return consumption; }
}

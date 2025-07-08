package com.example.global.DAO;

import java.time.LocalDateTime;

public class FeederReading {
    private LocalDateTime dateAndTime;
    private String feederDetails;  // e.g. "[LT_Panel1]N2MFM14"
    private Double current;        // from tag ending with 'A'
    private Double kw;             // from tag ending with 'KW'
    private Double kwh;            // from tag ending with 'KWH'
    private Double voltage;        // from tag ending with 'V'

    // Constructors
    public FeederReading(LocalDateTime dateAndTime, String feederDetails) {
        this.dateAndTime = dateAndTime;
        this.feederDetails = feederDetails;
    }

    // Getters and Setters
    public LocalDateTime getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(LocalDateTime dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getFeederDetails() {
        return feederDetails;
    }

    public void setFeederDetails(String feederDetails) {
        this.feederDetails = feederDetails;
    }

    public Double getCurrent() {
        return current;
    }

    public void setCurrent(Double current) {
        this.current = current;
    }

    public Double getKw() {
        return kw;
    }

    public void setKw(Double kw) {
        this.kw = kw;
    }

    public Double getKwh() {
        return kwh;
    }

    public void setKwh(Double kwh) {
        this.kwh = kwh;
    }

    public Double getVoltage() {
        return voltage;
    }

    public void setVoltage(Double voltage) {
        this.voltage = voltage;
    }
}


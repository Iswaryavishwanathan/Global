package com.example.global.DAO;

import java.time.LocalDateTime;

public class MergedReadingDAO {

    private LocalDateTime dateTime;
    private String tagName;
    private Double kw;
    private Double vll;
    private Double current;
    private Double kwh;

    // Constructor with all fields
    public MergedReadingDAO(LocalDateTime dateTime, String tagName,
                            Double kw, Double vll, Double current, Double kwh) {
        this.dateTime = dateTime;
        this.tagName = tagName;
        this.kw = kw;
        this.vll = vll;
        this.current = current;
        this.kwh = kwh;
    }

    // Getters and setters
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

    public Double getKw() {
        return kw;
    }

    public void setKw(Double kw) {
        this.kw = kw;
    }

    public Double getVll() {
        return vll;
    }

    public void setVll(Double vll) {
        this.vll = vll;
    }

    public Double getCurrent() {
        return current;
    }

    public void setCurrent(Double current) {
        this.current = current;
    }

    public Double getKwh() {
        return kwh;
    }

    public void setKwh(Double kwh) {
        this.kwh = kwh;
    }
}

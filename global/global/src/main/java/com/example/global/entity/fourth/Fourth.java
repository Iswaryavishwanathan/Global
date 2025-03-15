package com.example.global.entity.fourth;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
@Entity
@Table(name = "FloatTable", schema = "dbo")
public class Fourth {
    @Id
    @Column(name = "DateAndTime")
    private LocalDateTime dateAndTime;
    @Column(name = "Millitm")
    private int millitm;
    @Column(name = "TagIndex")
    private int tagIndex;
    @Column(name = "Val")
    private double val;
    @Column(name = "Status")
    private String status;
    @Column(name = "Marker")
    private String marker;
    @Transient  // This field will not be stored in DB
    private String databaseSource;
    // Getters and Setters

    public String getDatabaseSource() {
        return databaseSource;
    }
    public void setDatabaseSource(String databaseSource) {
        this.databaseSource = databaseSource;
    }
    public LocalDateTime getDateAndTime() { return dateAndTime; }
    public void setDateAndTime(LocalDateTime dateAndTime) { this.dateAndTime = dateAndTime; }

    public int getMillitm() { return millitm; }
    public void setMillitm(int millitm) { this.millitm = millitm; }

    public int getTagIndex() { return tagIndex; }
    public void setTagIndex(int tagIndex) { this.tagIndex = tagIndex; }

    public double getVal() { return val; }
    public void setVal(int val) { this.val = val; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMarker() { return marker; }
    public void setMarker(String marker) { this.marker = marker; }

}

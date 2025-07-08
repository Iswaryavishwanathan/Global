package com.example.global.entity.third;
import java.time.LocalDateTime;

import com.example.global.entity.TagTable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name = "FloatTable", schema = "dbo")
public class Third {
     @Id
    @Column(name = "DateAndTime")
    private LocalDateTime dateAndTime;

    @Column(name = "Millitm")
    private int millitm;

    @ManyToOne
    @JoinColumn(name = "TagIndex", referencedColumnName = "TagIndex") // Foreign key mapping
    private TagTable tagTable;

    @Column(name = "Val")
    private double val;

    @Column(name = "Status")
    private String status;

    @Column(name = "Marker")
    private String marker;

    // Getters and Setters

    public LocalDateTime getDateAndTime() { return dateAndTime; }
    public void setDateAndTime(LocalDateTime dateAndTime) { this.dateAndTime = dateAndTime; }

    public int getMillitm() { return millitm; }
    public void setMillitm(int millitm) { this.millitm = millitm; }

    public TagTable getTagTable() { return tagTable; }
    public void setTagTable(TagTable tagTable) { this.tagTable = tagTable; }

    public double getVal() { return val; }
    public void setVal(double val) { this.val = val; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMarker() { return marker; }
    public void setMarker(String marker) { this.marker = marker; }



}

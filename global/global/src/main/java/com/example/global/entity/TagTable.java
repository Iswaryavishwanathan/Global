package com.example.global.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TagTable", schema = "dbo") // Ensure correct schema for SQL Server
@Getter
@Setter
public class TagTable {

    @Id
    @Column(name = "TagIndex")
    private Integer tagIndex;

    @Column(name = "TagName")
    private String tagName;

    @Column(name = "TagType")
    private String tagType;

    @Column(name = "TagDataType")
    private String tagDataType;
}


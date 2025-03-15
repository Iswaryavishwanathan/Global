package com.example.global.Service;

import com.example.global.entity.fourth.Fourth;
import com.example.global.entity.primary.Primary;
import com.example.global.entity.second.Secondary;
import com.example.global.entity.third.Third;
import com.example.global.Repository.fourth.FourthRepository;
import com.example.global.Repository.primary.PrimaryRepository;
import com.example.global.Repository.second.SecondaryRepository;
import com.example.global.Repository.third.ThirdRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataService {

    @Autowired
    private PrimaryRepository primaryRepository;

    @Autowired
    private SecondaryRepository secondaryRepository;

    @Autowired
    private ThirdRepository thirdRepository;

    @Autowired
    private FourthRepository fourthRepository;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public List<Object> getAllDataByDateRange(String startDate, String endDate) {
        try {
            System.out.println("Received Start Date: " + startDate);
            System.out.println("Received End Date: " + endDate);

            LocalDateTime startDateTime = LocalDateTime.parse(startDate, dateFormatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDate, dateFormatter);

            System.out.println("Parsed Start DateTime: " + startDateTime);
            System.out.println("Parsed End DateTime: " + endDateTime);

            List<Primary> primaryData = primaryRepository.findByDateRange(startDateTime, endDateTime);
            List<Secondary> secondaryData = secondaryRepository.findByDateRange(startDateTime, endDateTime);
            List<Third> thirdData = thirdRepository.findByDateRange(startDateTime, endDateTime);
            List<Fourth> fourthData = fourthRepository.findByDateRange(startDateTime, endDateTime);
             // 🔹 Set databaseSource manually for each entity type
        primaryData.forEach(item -> item.setDatabaseSource("LTPanel1"));
        secondaryData.forEach(item -> item.setDatabaseSource("LTPanel2"));
        thirdData.forEach(item -> item.setDatabaseSource("LTPanel3"));
        fourthData.forEach(item -> item.setDatabaseSource("SevenVCB"));
        // for (Primary p : primaryData) {
        //     System.out.println("Fetched -> TagIndex: " + p.getTagIndex());
        // }
        
            return Stream.of(primaryData, secondaryData, thirdData, fourthData)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error in getAllDataByDateRange: " + e.getMessage());
            e.printStackTrace();
            return null; // or throw a custom exception
        }
    }
}

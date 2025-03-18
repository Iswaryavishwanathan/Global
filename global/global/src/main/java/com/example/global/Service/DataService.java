// package com.example.global.Service;

// import com.example.global.DAO.FourthDAO.FourthDAO;
// import com.example.global.DAO.PrimaryDAO.PrimaryDAO;
// import com.example.global.DAO.SecondaryDAO.SecondaryDAO;
// import com.example.global.DAO.ThirdDAO.ThirdDAO;
// import com.example.global.Repository.fourth.FourthRepository;
// import com.example.global.Repository.primary.PrimaryRepository;
// import com.example.global.Repository.second.SecondaryRepository;
// import com.example.global.Repository.third.ThirdRepository;
// import com.example.global.entity.fourth.Fourth;
// import com.example.global.entity.primary.Primary;
// import com.example.global.entity.second.Secondary;
// import com.example.global.entity.third.Third;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.List;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;

// @Service
// public class DataService {

//     @Autowired
//     private PrimaryRepository primaryRepository;

//     @Autowired
//     private SecondaryRepository secondaryRepository;

//     @Autowired
//     private ThirdRepository thirdRepository;

//     @Autowired
//     private FourthRepository fourthRepository;

//     @Autowired
//     private DataConversionService dataConversionService;  // 🔹 Use conversion service

//     private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

//     public List<Object> getAllDataByDateRange(String startDate, String endDate) {
//         try {
//             System.out.println("Received Start Date: " + startDate);
//             System.out.println("Received End Date: " + endDate);

//             LocalDateTime startDateTime = LocalDateTime.parse(startDate, dateFormatter);
//             LocalDateTime endDateTime = LocalDateTime.parse(endDate, dateFormatter);

//             System.out.println("Parsed Start DateTime: " + startDateTime);
//             System.out.println("Parsed End DateTime: " + endDateTime);

//             // Fetch entity data from repositories
//             List<PrimaryDAO> primaryEntities = primaryRepository.findByDateRange(startDateTime, endDateTime);
//             List<SecondaryDAO> secondaryEntities = secondaryRepository.findByDateRange(startDateTime, endDateTime);
//             List<ThirdDAO> thirdEntities = thirdRepository.findByDateRange(startDateTime, endDateTime);
//             List<FourthDAO> fourthEntities = fourthRepository.findByDateRange(startDateTime, endDateTime);

//             // Convert entities to DAOs using DataConversionService
//             List<PrimaryDAO> primaryData = dataConversionService.convertAllPrimary(primaryEntities);
//             List<SecondaryDAO> secondaryData = dataConversionService.convertAllSecondary(secondaryEntities);
//             List<ThirdDAO> thirdData = dataConversionService.convertAllThird(thirdEntities);
//             List<FourthDAO> fourthData = dataConversionService.convertAllFourth(fourthEntities);

//             // ✅ Log the counts
//             System.out.println("Primary Data Count: " + primaryData.size());
//             System.out.println("Secondary Data Count: " + secondaryData.size());
//             System.out.println("Third Data Count: " + thirdData.size());
//             System.out.println("Fourth Data Count: " + fourthData.size());

//             // Combine all data
//             List<Object> allData = Stream.of(primaryData, secondaryData, thirdData, fourthData)
//                     .flatMap(List::stream)
//                     .collect(Collectors.toList());

//             // ✅ Log total fetched data count
//             System.out.println("Total Data Fetched: " + allData.size());

//             return allData;

//         } catch (Exception e) {
//             System.err.println("Error in getAllDataByDateRange: " + e.getMessage());
//             e.printStackTrace();
//             return null; // or throw a custom exception
//         }
//     }
// }
package com.example.global.Service;

import com.example.global.DAO.FourthDAO.FourthDAO;
import com.example.global.DAO.PrimaryDAO.PrimaryDAO;
import com.example.global.DAO.SecondaryDAO.SecondaryDAO;
import com.example.global.DAO.ThirdDAO.ThirdDAO;
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

            // Fetch data as DTOs from repositories
            List<PrimaryDAO> primaryEntities = primaryRepository.findByDateRange(startDateTime, endDateTime);
            List<SecondaryDAO> secondaryEntities = secondaryRepository.findByDateRange(startDateTime, endDateTime);
            List<ThirdDAO> thirdEntities = thirdRepository.findByDateRange(startDateTime, endDateTime);
            List<FourthDAO> fourthEntities = fourthRepository.findByDateRange(startDateTime, endDateTime);

            // ✅ Log the counts
            System.out.println("Primary Data Count: " + primaryEntities.size());
            System.out.println("Secondary Data Count: " + secondaryEntities.size());
            System.out.println("Third Data Count: " + thirdEntities.size());
            System.out.println("Fourth Data Count: " + fourthEntities.size());

            // Combine all data
            List<Object> allData = Stream.of(primaryEntities, secondaryEntities, thirdEntities, fourthEntities)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            // ✅ Log total fetched data count
            System.out.println("Total Data Fetched: " + allData.size());

            return allData;

        } catch (Exception e) {
            System.err.println("Error in getAllDataByDateRange: " + e.getMessage());
            e.printStackTrace();
            return null; // or throw a custom exception
        }
    }
}

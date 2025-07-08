package com.example.global.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledReportService {

    @Autowired
    private KWhConsumptionService kWhConsumptionService;

    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // @Scheduled(cron = "0 59 23 * * ?") // Every day at 23:59
    // @Scheduled(cron = "0 0/2 * * * ?") // Every 2 minutes
    @Scheduled(cron = "59 0 0 * * ?")
    public void generateDailyReport() {
        LocalDate today = LocalDate.now();
       
LocalDateTime startOfDay = today.atStartOfDay(); // 00:00
// LocalDateTime endOfDay = today.atTime(LocalTime.MAX); // 23:59:59.999999999
LocalDateTime endOfDay = LocalDate.now().plusDays(1).atTime(0, 10); // 00:10 tomorrow


        try {
            Workbook workbook = kWhConsumptionService.exportConsumptionData(startOfDay, endOfDay);

            // Define output directory and file
            String userHome = System.getProperty("user.home");
            String outputDir = userHome + "/Desktop/EnergyReports/";
            Files.createDirectories(Paths.get(outputDir));

            String fileName = "EnergyConsumption_" + today.format(FILE_DATE_FORMAT) + ".xlsx";
            File outputFile = new File(outputDir + fileName);

            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                workbook.write(out);
                System.out.println("Report saved: " + outputFile.getAbsolutePath());
            }

            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


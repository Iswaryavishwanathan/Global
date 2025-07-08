package com.example.global.Controller;

import com.example.global.DAO.FeederReading;
import com.example.global.DAO.KWhConsumptionDAO;
import com.example.global.Service.DataService;
import com.example.global.Service.KWhConsumptionService;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/data")
public class DataController {

    @Autowired
    private DataService dataService;
    @Autowired
    private KWhConsumptionService kWhConsumptionService;

    @GetMapping("/range")
    public ResponseEntity<List<FeederReading>> getMergedData(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<FeederReading> data = dataService.mergeReadings(startDate, endDate);
        return ResponseEntity.ok(data);
    }
    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportMergedExcelReport(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {

        String start = startDateTime.toString(); 
        String end = endDateTime.toString();      

        byte[] excelData = dataService.generateExcelReport(start, end);

        if (excelData == null || excelData.length == 0) {
            return ResponseEntity.noContent().build();
        }

        String fileName = "Merged_Report_" + startDateTime.toLocalDate() + "_to_" + endDateTime.toLocalDate() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }
    @GetMapping("/daily")
    public List<KWhConsumptionDAO> getConsumption(
        @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime start,
        @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime end) {
        return kWhConsumptionService.getConsumptionForDate(start, end);
    }


    @GetMapping("/export-consumption")
public void exportConsumptionReport(
        @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime start,
        @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime end,
        HttpServletResponse response) throws IOException {

    // Generate the workbook using the template and single date
    Workbook workbook = kWhConsumptionService.exportConsumptionData(start, end);

    // Format the start and end times for filename
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
    String formattedStart = start.format(formatter);
    String formattedEnd = end.format(formatter);
    String filename = "EnergyConsumption_" + formattedStart + "_to_" + formattedEnd + ".xlsx";

    // Set response headers
    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition", "attachment; filename=" + filename);

    // Stream the workbook to the response
    workbook.write(response.getOutputStream());
    workbook.close();
}

}

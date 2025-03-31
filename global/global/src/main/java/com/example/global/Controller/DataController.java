package com.example.global.Controller;

import com.example.global.DAO.CommonDAO;
import com.example.global.DAO.KWhConsumptionDAO;
import com.example.global.Service.DataService;
import com.example.global.Service.KWhConsumptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/data")
public class DataController {

    @Autowired
    private DataService dataService;
    @Autowired
    private KWhConsumptionService kWhConsumptionService;

    @GetMapping("/range")
    public List<CommonDAO> getAllDataByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return dataService.getAllDataByDateRange(startDate, endDate);
    }
    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel(@RequestParam String startDate, 
                                              @RequestParam String endDate, 
                                              @RequestParam(defaultValue = "All") String databaseSource) {
        byte[] excelData = dataService.generateExcelReport(startDate, endDate, databaseSource);
    
        if (excelData == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data_report.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }
    @GetMapping("/daily")
    public List<KWhConsumptionDAO> getConsumption(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return kWhConsumptionService.getConsumptionForDate(date);
    }

    @GetMapping("/export/kwh-report")
    public ResponseEntity<byte[]> exportKWhReport(@RequestParam("date") String date) {
        try {
            LocalDate selectedDate = LocalDate.parse(date);
            byte[] excelData = kWhConsumptionService.generateExcelReport(selectedDate); // Fixed variable name
    
            if (excelData == null) {
                return ResponseEntity.noContent().build();
            }
    
            String fileName = "KWh_Consumption_Report_" + selectedDate + ".xlsx";
    
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(("Error generating report: " + e.getMessage()).getBytes());
        }
    }
    
}

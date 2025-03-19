package com.example.global.Controller;

import com.example.global.DAO.CommonDAO;
import com.example.global.Service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/data")
public class DataController {

    @Autowired
    private DataService dataService;

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
}

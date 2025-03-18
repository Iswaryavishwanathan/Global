package com.example.global.Controller;

import com.example.global.Service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/data")
public class DataController {

    @Autowired
    private DataService dataService;

    @GetMapping("/range")
    public List<Object> getAllDataByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return dataService.getAllDataByDateRange(startDate, endDate);
    }

}

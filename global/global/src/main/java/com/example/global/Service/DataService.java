package com.example.global.Service;

import com.example.global.DAO.CommonDAO;
import com.example.global.Repository.fourth.FourthRepository;
import com.example.global.Repository.primary.PrimaryRepository;
import com.example.global.Repository.second.SecondaryRepository;
import com.example.global.Repository.third.ThirdRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    @Autowired
    private TagMappingService tagMappingService;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public List<CommonDAO> getAllDataByDateRange(String startDate, String endDate) {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(startDate, dateFormatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDate, dateFormatter);

            // Fetch Data from Repositories
            List<CommonDAO> primaryData = primaryRepository.findByDateRange(startDateTime, endDateTime);
            List<CommonDAO> secondaryData = secondaryRepository.findByDateRange(startDateTime, endDateTime);
            List<CommonDAO> thirdData = thirdRepository.findByDateRange(startDateTime, endDateTime);
            List<CommonDAO> fourthData = fourthRepository.findByDateRange(startDateTime, endDateTime);

            // Modify Tag Names using TagMappingService
            List<CommonDAO> modifiedPrimary = modifyTagNames(primaryData, tagMappingService.getPrimaryTagMapping());
            List<CommonDAO> modifiedSecondary = modifyTagNames(secondaryData, tagMappingService.getSecondaryTagMapping());
            List<CommonDAO> modifiedThird = modifyTagNames(thirdData, tagMappingService.getThirdTagMapping());
            List<CommonDAO> modifiedFourth = modifyTagNames(fourthData, tagMappingService.getFourthTagMapping());

            // Combine All Data
            return Stream.of(modifiedPrimary, modifiedSecondary, modifiedThird, modifiedFourth)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public byte[] generateExcelReport(String startDate, String endDate, String databaseSource) {
        List<CommonDAO> dataList = getAllDataByDateRange(startDate, endDate);

        // ✅ Filter by selected database source
        if (!databaseSource.equalsIgnoreCase("All")) {
            dataList = dataList.stream()
                    .filter(data -> data.getDatabaseSource().equalsIgnoreCase(databaseSource))
                    .collect(Collectors.toList());
        }

        if (dataList.isEmpty()) return null; // No data to export

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Report");

            // ✅ Add Title Row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Data Report from " + startDate + " to " + endDate + " for " + databaseSource);
            titleCell.setCellStyle(getHeaderCellStyle(workbook));
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

            // ✅ Create Header Row
            Row headerRow = sheet.createRow(1);
            String[] headers = {"Date & Time", "Millitm", "Tag Index", "Tag Name", "Value", "Status", "Marker", "Database Source"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(getHeaderCellStyle(workbook));
            }

            // ✅ Write Data Rows
            int rowNum = 2;
            for (CommonDAO data : dataList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.getDateAndTime().toString());
                row.createCell(1).setCellValue(data.getMillitm());
                row.createCell(2).setCellValue(data.getTagIndex());
                row.createCell(3).setCellValue(data.getTagName());
                row.createCell(4).setCellValue(data.getVal());
                row.createCell(5).setCellValue(data.getStatus());
                row.createCell(6).setCellValue(data.getMarker());
                row.createCell(7).setCellValue(data.getDatabaseSource());
            }

            // ✅ Add Total Count Row
            Row totalRow = sheet.createRow(rowNum);
            totalRow.createCell(0).setCellValue("Total Records:");
            totalRow.createCell(1).setCellValue(dataList.size());

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            // Convert to Byte Array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<CommonDAO> modifyTagNames(List<CommonDAO> dataList, Map<String, String> tagMapping) {
        return dataList.stream().map(data -> {
            String newTagName = tagMapping.getOrDefault(data.getTagName(), data.getTagName());
            data.setTagName(newTagName);
            return data;
        }).collect(Collectors.toList());
    }

    /**
     * Create a header cell style.
     */
    private CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}

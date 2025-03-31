package com.example.global.Service;

import com.example.global.DAO.KWhConsumptionDAO;
import com.example.global.Repository.primary.PrimaryRepository;
import com.example.global.Repository.second.SecondaryRepository;
import com.example.global.Repository.third.ThirdRepository;
import com.example.global.Repository.fourth.FourthRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KWhConsumptionService {

    @Autowired
    private PrimaryRepository primaryRepository;

    @Autowired
    private SecondaryRepository secondaryRepository;

    @Autowired
    private ThirdRepository thirdRepository;

    @Autowired
    private FourthRepository fourthRepository;


    public List<KWhConsumptionDAO> getConsumptionForDate(LocalDate selectedDate) {
        LocalDateTime startOfDay = selectedDate.atStartOfDay();
        LocalDateTime endOfDay = selectedDate.atTime(LocalTime.MAX);
        
        System.out.println("Fetching consumption data from: " + startOfDay + " to " + endOfDay);

        List<KWhConsumptionDAO> result = new ArrayList<>();

        // Fetch data from all repositories
        List<KWhConsumptionDAO> primaryData = primaryRepository.findConsumptionData(startOfDay, endOfDay);
        List<KWhConsumptionDAO> secondaryData = secondaryRepository.findConsumptionData(startOfDay, endOfDay);
        List<KWhConsumptionDAO> thirdData = thirdRepository.findConsumptionData(startOfDay, endOfDay);
        List<KWhConsumptionDAO> fourthData = fourthRepository.findConsumptionData(startOfDay, endOfDay);

        // Modify Tag Names using TagMappingService
        List<KWhConsumptionDAO> modifiedPrimary = modifyTagNames(primaryData, getPrimaryTagMapping());
        List<KWhConsumptionDAO> modifiedSecondary = modifyTagNames(secondaryData, getSecondaryTagMapping());
        List<KWhConsumptionDAO> modifiedThird = modifyTagNames(thirdData, getThirdTagMapping());
        List<KWhConsumptionDAO> modifiedFourth = modifyTagNames(fourthData, getFourthTagMapping());

        System.out.println("Primary Data: " + modifiedPrimary.size());
        System.out.println("Secondary Data: " + modifiedSecondary.size());
        System.out.println("Third Data: " + modifiedThird.size());
        System.out.println("Fourth Data: " + modifiedFourth.size());

        // Add modified data to result
        result.addAll(modifiedPrimary);
        result.addAll(modifiedSecondary);
        result.addAll(modifiedThird);
        result.addAll(modifiedFourth);

        return result;
    }
/**
     * Generates an Excel report for the given date.
     */
    public byte[] generateExcelReport(LocalDate selectedDate) {
        List<KWhConsumptionDAO> dataList = getConsumptionForDate(selectedDate);
    
        if (dataList.isEmpty()) {
            return null; // No data available
        }
    
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("KWh Consumption Report");
    
            // Define styles
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle centerAlignedStyle = createFullyCenteredStyle(workbook);
            CellStyle borderedStyle = createBorderedStyle(workbook);
    
            int rowNum = 0;
    
            // **Title Row**
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Nxtra SS Ground Floor");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7)); // Merge across all columns
    
            // **Header Row**
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"S.No", "Panel Name", "Feeder Details", "Start DateTime", "End DateTime", "Start Value", "End Value", "Consumption"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
    
            int serialNumber = 1;
            String previousDatabaseName = "";
            int mergeStartRow = -1;
    
            // Define custom order of database names
            List<String> orderedDatabases = Arrays.asList("NXTRA SINGLE VCB", "SevenVCB", "LTPanel3", "LTPanel2", "LTPanel1");
    
            // Group data by database names
            Map<String, List<KWhConsumptionDAO>> groupedData = new LinkedHashMap<>();
            for (String dbName : orderedDatabases) {
                groupedData.put(dbName, new ArrayList<>());
            }
            
            for (KWhConsumptionDAO entry : dataList) {
                String dbName = entry.getDatabaseName();
                if (dbName.equals("LTPanel2") && entry.getTagName().equals("INCOMER")) {
                    groupedData.get("NXTRA SINGLE VCB").add(entry);
                } else if (groupedData.containsKey(dbName)) {
                    groupedData.get(dbName).add(entry);
                }
            }
    
            // Insert records in the defined order
            for (String dbName : orderedDatabases) {
                List<KWhConsumptionDAO> entries = groupedData.get(dbName);
                for (KWhConsumptionDAO entry : entries) {
                    Row row = sheet.createRow(rowNum++);
                    fillRowWithData(row, serialNumber++, dbName, entry, centerAlignedStyle, borderedStyle);
    
                    // Handle merging of database name cells
                    if (!dbName.equals(previousDatabaseName)) {
                        if (mergeStartRow != -1) {
                            sheet.addMergedRegion(new CellRangeAddress(mergeStartRow, rowNum - 2, 1, 1));
                        }
                        mergeStartRow = rowNum - 1;
                        previousDatabaseName = dbName;
                    }
                }
            }
    
            // Merge last section
            if (mergeStartRow != -1) {
                sheet.addMergedRegion(new CellRangeAddress(mergeStartRow, rowNum - 1, 1, 1));
            }
    
            // Auto-size all columns for better readability
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
    
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    
    // **Helper method to fill a row with data**
    private void fillRowWithData(Row row, int serialNumber, String databaseName, KWhConsumptionDAO entry, CellStyle centerAlignedStyle, CellStyle borderedStyle) {
        row.createCell(0).setCellValue(serialNumber);
    
        Cell dbNameCell = row.createCell(1);
        dbNameCell.setCellValue(databaseName);
        dbNameCell.setCellStyle(centerAlignedStyle);
    
        row.createCell(2).setCellValue(entry.getTagName()); 
        row.createCell(3).setCellValue(entry.getStartDateTime().toString());
        row.createCell(4).setCellValue(entry.getEndDateTime().toString());
        row.createCell(5).setCellValue(entry.getStartValue());
        row.createCell(6).setCellValue(entry.getEndValue());
        row.createCell(7).setCellValue(entry.getConsumption());
    
        // Apply border style to all cells
        for (int i = 0; i <= 7; i++) {
            row.getCell(i).setCellStyle(borderedStyle);
        }
    }
    
    // **Title Style (Blue Background, White Text)**
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
    
    // **Header Style (Gray Background, Bold Text)**
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    // **Fully Centered Style for Database Names**
    private CellStyle createFullyCenteredStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    // **Bordered Style for all cells**
    private CellStyle createBorderedStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

      /**
     * Modifies the tag names of the given KWhConsumptionDAO list using the provided tag mapping.
    //  */
    private List<KWhConsumptionDAO> modifyTagNames(List<KWhConsumptionDAO> dataList, Map<String, String> tagMapping) {
        return dataList.stream()
                .map(data -> {
                    String newTagName = tagMapping.getOrDefault(data.getTagName().trim(), data.getTagName());
                    return new KWhConsumptionDAO( data.getDatabaseName(), newTagName, data.getStartDateTime(), data.getEndDateTime(),
                                                 data.getStartValue(), data.getEndValue());
                })
                .collect(Collectors.toList());
    }
    
      public Map<String, String> getPrimaryTagMapping() {
        Map<String, String> map = new HashMap<>();
        map.put("[LT_Panel1]MFM02KWH", "O/G TO DG-2 COIL COOLER PANEL");
        map.put("[LT_Panel1]MFM03KWH", "O/G TO MLDB Panel");
        map.put("[LT_Panel1]MFM04KWH", "O/G TO FIRE HYDRANT PANEL ");
        map.put("[LT_Panel1]MFM05KWH", "I/C FROM TRANSFORMER - 1");
        map.put("[LT_Panel1]MFM06KWH", "DG INCOMMER - A");
        map.put("[LT_Panel1]MFM07KWH", "O/G TO RM-2B");
        map.put("[LT_Panel1]MFM08KWH", "O/G TO RM-1B");
        map.put("[LT_Panel1]MFM09KWH", "O/G TO MPDB PANEL");
        map.put("[LT_Panel1]MFM10KWH", "DG INCOMER - B");
        map.put("[LT_Panel1]MFM11KWH", "O/G TO RM - 2A");
        return map;
    }

    public Map<String, String> getSecondaryTagMapping() {
        Map<String, String> map = new HashMap<>();   
        map.put("[LT_Panel1]MFM12KWH", "O/G TO 4F LT Panel-3A"); 
        map.put("[LT_Panel1]MFM13KWH", "I/C FROM TRANSFORMER-4"); 
        map.put("[LT_Panel1]MFM14KWH", "O/G TO RM - 3A");  
        map.put("[LT_Panel1]MFM15KWH", "7F A2 Hall LT Panel 3A");  
        map.put("[LT_Panel1]MFM16KWH", "I/C FROM TRANSFORMER-3");   
        map.put("[LT_Panel1]MFM17KWH", "DG INCOMER-C");     
        map.put("[LT_Panel1]MFM18KWH", "O/G TO RM - 4A");    
        map.put("[LT_Panel1]MFM19KWH", "INCOMER");       
        return map;
    }

    public Map<String, String> getThirdTagMapping() {
        Map<String, String> map = new HashMap<>();
        map.put("[7VCB]MFM09_WH", "O/G to 7th Floor LT-Panel 3B");
        map.put("[7VCB]MFM10_WH", "O/G TO RM-4B");
        map.put("[7VCB]MFM11_WH", "TRANSFORMER-6"); 
        map.put("[7VCB]MFM12_WH", "DG INCOME-D");
        map.put("[7VCB]MFM13_WH", "O/G TO RM-3B");
        map.put("[7VCB]MFM14_WH", "TRANSFORMER-5");
        map.put("[7VCB]MFM15_WH", "O/G TO RM 5A");
        return map;
    }

    public Map<String, String> getFourthTagMapping() {
        Map<String, String> map = new HashMap<>();
        map.put("[7VCB]MFM02_WH", "INCOMER");
        map.put("[7VCB]MFM05_WH", "TRANSFORMER -3");
        map.put("[7VCB]MFM06_WH", "TRANSFORMER -4");
        map.put("[7VCB]MFM07_WH", "TRANSFORMER -5");
        map.put("[7VCB]MFM08_WH", "TRANSFORMER -6");
        return map;
    }

    

    }


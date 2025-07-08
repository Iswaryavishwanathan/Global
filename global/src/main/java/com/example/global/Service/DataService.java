package com.example.global.Service;

import com.example.global.DAO.CommonDAO;
import com.example.global.DAO.FeederReading;
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
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public List<FeederReading> mergeReadings(LocalDateTime start, LocalDateTime end) {
         end = end.withSecond(59).withNano(999_000_000);
        List<CommonDAO> vllReadings = primaryRepository.findReadings(start, end);
        List<CommonDAO> currentReadings = secondaryRepository.findReadings(start, end);
        List<CommonDAO> kwReadings = thirdRepository.findReadings(start, end);
        List<CommonDAO> kwhReadings = fourthRepository.findReadings(start, end);

        Map<String, String> tagMapping = getPrimaryTagMapping();
        Map<String, FeederReading> mergedMap = new HashMap<>();

        Function<String, String> feederKeyFunc = tagName -> {
            if (tagName == null) return "";
            return tagName.replaceAll("(A|KW|KWH|VLL|WH)$", "").trim();
        };

        Function<CommonDAO, String> mapKeyFunc = r ->
            r.getDateTime().toString() + "_" + feederKeyFunc.apply(r.getTagName());



for (CommonDAO r : vllReadings) {
    String rawName = feederKeyFunc.apply(r.getTagName());
    if (!tagMapping.containsKey(rawName)) continue;
    createOrUpdateFeederReading(mergedMap, mapKeyFunc.apply(r), r, tagMapping).setVoltage(r.getVal());
}

for (CommonDAO r : currentReadings) {
    String rawName = feederKeyFunc.apply(r.getTagName());
    if (!tagMapping.containsKey(rawName)) continue;
    createOrUpdateFeederReading(mergedMap, mapKeyFunc.apply(r), r, tagMapping).setCurrent(r.getVal());
}

    for (CommonDAO r : kwReadings) {
    String rawName = feederKeyFunc.apply(r.getTagName());
    if (!tagMapping.containsKey(rawName)) continue; // skip unmapped
    createOrUpdateFeederReading(mergedMap, mapKeyFunc.apply(r), r, tagMapping).setKw(r.getVal());
}

for (CommonDAO r : kwhReadings) {
    String rawName = feederKeyFunc.apply(r.getTagName());
    if (!tagMapping.containsKey(rawName)) continue;
    createOrUpdateFeederReading(mergedMap, mapKeyFunc.apply(r), r, tagMapping).setKwh(r.getVal());
}


        return mergedMap.values().stream()
                .sorted(Comparator.comparing(FeederReading::getDateAndTime))
                .collect(Collectors.toList());
    }

    private FeederReading createOrUpdateFeederReading(Map<String, FeederReading> map, String key, CommonDAO r, Map<String, String> tagMap) {
        FeederReading fr = map.get(key);
        if (fr == null) {
            String rawName = r.getTagName().replaceAll("(A|KW|KWH|VLL|WH)$", "").trim();
            String displayName = tagMap.getOrDefault(rawName, rawName);
            fr = new FeederReading(r.getDateTime(), displayName);
            map.put(key, fr);
        }
        return fr;
    }

    public byte[] generateExcelReport(String startDate, String endDate) {
        List<FeederReading> dataList = mergeReadings(
            LocalDateTime.parse(startDate, dateFormatter),
            LocalDateTime.parse(endDate, dateFormatter).withSecond(59).withNano(999_000_000));

        if (dataList.isEmpty()) return null;

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Merged Data");

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Merged Data Report from " + startDate + " to " + endDate);
            titleCell.setCellStyle(getHeaderCellStyle(workbook));
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            Row headerRow = sheet.createRow(1);
            String[] headers = {"Date & Time", "Feeder Details", "VLL", "Current", "KH", "KWH"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(getHeaderCellStyle(workbook));
            }

            int rowNum = 2;
            for (FeederReading data : dataList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.getDateAndTime().toString());
                row.createCell(1).setCellValue(data.getFeederDetails());
                setNullableDouble(row.createCell(2), data.getVoltage());
                setNullableDouble(row.createCell(3), data.getCurrent());
                setNullableDouble(row.createCell(4), data.getKw());
                setNullableDouble(row.createCell(5), data.getKwh());
            }

            Row totalRow = sheet.createRow(rowNum);
            totalRow.createCell(0).setCellValue("Total Records:");
            totalRow.createCell(1).setCellValue(dataList.size());

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

private void setNullableDouble(Cell cell, Double value) {
    cell.setCellValue(round(value != null ? value : 0.0, 2));
}


    private CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

   public Map<String, String> getPrimaryTagMapping() {
        Map<String, String> map = new HashMap<>();
        map.put("[LT_Panel1]N2MFM09", "Nxtra Single VCB-Incomer");
        map.put("[7VCB]MFM02", "7VCB-Incomer");
        map.put("[7VCB]MFM03", "7VCB-Spare Tranformer Feeder");
        map.put("[7VCB]MFM04", "7VCB-Transformer-7");
        map.put("[7VCB]MFM05", "7VCB-Transformer-3");
        map.put("[7VCB]MFM06", "7VCB-Transformer-4");
        map.put("[7VCB]MFM07", "7VCB-Transformer-5");
        map.put("[7VCB]MFM08", "7VCB-Transformer-6"); 
        map.put("[7VCB]MFM11", "LTP3-Transformer-6");
        map.put("[7VCB]MFM14", "LTP3-Transformer-5");
        map.put("[7VCB]MFM12", "LTP3-DG IC-D");
        map.put("[7VCB]MFM09", "LTP3-OG To 7FL LTPanel 3B");
        map.put("[7VCB]MFM10", "LTP3-OG To RM-4B");
        map.put("[7VCB]MFM13", "LTP3-OG To RM-3B");
        map.put("[7VCB]MFM15", "LTP3-OG To RM-5A");
        map.put("[LT_Panel1]N1MFM15", "LTP1-Transformer-7");
        map.put("[LT_Panel1]N1MFM30", "LTP1-IC From Transformer-2");
        map.put("[LT_Panel1]N1MFM05", "LTP1-IC From Transformer-1");
        map.put("[LT_Panel1]N1MFM12", "LTP1-OG To GF Smoke Exhaust Panel-A");
        map.put("[LT_Panel1]N1MFM06", "LTP1-DG IC-A");
        map.put("[LT_Panel1]N1MFM10", "LTP1-DG IC-B");
        map.put("[LT_Panel1]N1MFM02", "LTP1-OG GF Freshair Panel-A");
        map.put("[LT_Panel1]N1MFM03", "LTP1-OG To GF MLDB Panel");
        map.put("[LT_Panel1]N1MFM04", "LTP1-OG TO Fire Hydrant Panel");
        map.put("[LT_Panel1]N1MFM07", "LTP1-OG To RM-2B");
        map.put("[LT_Panel1]N1MFM08", "LTP1-OG To RM-1B");
        map.put("[LT_Panel1]N1MFM09", "LTP1-OG To MPDB Panel");
        map.put("[LT_Panel1]N1MFM19", "LTP1-OG To Spare");
        map.put("[LT_Panel1]N1MFM17", "LTP1-OG To GF Freshair Panel-B");
        map.put("[LT_Panel1]N1MFM18", "LTP1-OG To GF Smoke Exhaust Panel-B");
        map.put("[LT_Panel1]N1MFM20", "LTP1-Spare Feeder 1600A (16F)");
        map.put("[LT_Panel1]N1MFM11", "LTP1-OG To RM-2A");
        map.put("[LT_Panel1]N1MFM13", "LTP1-OG To RM-1A");
        map.put("[LT_Panel1]N1MFM14", "LTP1-Spare (2F3)");
        map.put("[LT_Panel1]N1MFM16", "LTP1-Spare Feeder 1600A (4F)");
        map.put("[LT_Panel1]N2MFM03", "LTP2-IC From Transformer-4");
        map.put("[LT_Panel1]N2MFM06", "LTP2-IC From Transformer-3");
        map.put("[LT_Panel1]N2MFM07", "LTP2-DG IC-C");
        map.put("[LT_Panel1]N2MFM02", "LTP2-OG To 4F LTPanel-3A");
        map.put("[LT_Panel1]N2MFM04", "LTP2-OG To RM-3A");
        map.put("[LT_Panel1]N2MFM08", "LTP2-OG To RM-4A");
        map.put("[LT_Panel1]N2MFM05", "LTP2-OG To 7FL LTPanel-3A");

        map.put("[LT_Panel1]N2MFM10", "Airtel Singe VCB - Incomer");
        map.put("[LT_Panel1]N2MFM11", "Airte 3VCB - Incomer");
        map.put("[LT_Panel1]N2MFM12", "Airtel 3VCB-OG To Transforment1");
        map.put("[LT_Panel1]N2MFM13", "Airtel 3VCB-OG To Transforment2");
        map.put("[LT_Panel1]N1MFM15", "DG Sync-IC From DG1");
        map.put("[LT_Panel1]N1MFM29", "DG Sync-DG6 PCC");
        map.put("[LT_Panel1]N1MFM28", "DG Sync-DG5 PCC");
        map.put("[LT_Panel1]N1MFM26", "DG Sync-DG3 PCC");
        map.put("[LT_Panel1]N1MFM27", "DG Sync-DG4 PCC");
        map.put("[LT_Panel1]N1MFM21", "DG Sync-IC From DG1");
        map.put("[LT_Panel1]N1MFM25", "DG Sync-IC From DG6");
        map.put("[LT_Panel1]N1MFM24", "DG Sync-IC From DG5");
        map.put("[LT_Panel1]N1MFM22", "DG Sync-IC From DG3");
        map.put("[LT_Panel1]N1MFM23", "DG Sync-IC From DG4");
        map.put("[LT_Panel_1A]TR1_", "LTP1A-IC From Transformer1");
        map.put("[LT_Panel_1A]DG1_", "LTP1A-IC From DG1");
        map.put("[LT_Panel_1A]TR2_", "LTP1B-IC From Transformer2");
        map.put("[LT_Panel_1A]DG2_", "LTP1B-IC From DG2");
        map.put("PCC1_Total_", "DG1 PCC");
        map.put("PCC2_Total_", "DG2 PCC");
        

        return map;
    }
}

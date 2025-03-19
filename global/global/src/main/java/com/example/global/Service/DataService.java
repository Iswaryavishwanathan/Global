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

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public List<CommonDAO> getAllDataByDateRange(String startDate, String endDate) {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(startDate, dateFormatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDate, dateFormatter);
            System.out.println("Parsed Start DateTime: " + startDateTime);
            System.out.println("Parsed End DateTime: " + endDateTime);

            // Fetch Data from Repositories
            List<CommonDAO> primaryData = primaryRepository.findByDateRange(startDateTime, endDateTime);
            List<CommonDAO> secondaryData = secondaryRepository.findByDateRange(startDateTime, endDateTime);
            List<CommonDAO> thirdData = thirdRepository.findByDateRange(startDateTime, endDateTime);
            List<CommonDAO> fourthData = fourthRepository.findByDateRange(startDateTime, endDateTime);

            // Modify Tag Names
            List<CommonDAO> modifiedPrimary = modifyTagNames(primaryData, getPrimaryTagMapping());
            List<CommonDAO> modifiedSecondary = modifyTagNames(secondaryData, getSecondaryTagMapping());
            List<CommonDAO> modifiedThird = modifyTagNames(thirdData, getThirdTagMapping());
            List<CommonDAO> modifiedFourth = modifyTagNames(fourthData, getFourthTagMapping());
            // ✅ Log the counts
            System.out.println("Primary Data Count: " + primaryData.size());
            System.out.println("Secondary Data Count: " + secondaryData.size());
            System.out.println("Third Data Count: " + thirdData.size());
            System.out.println("Fourth Data Count: " + fourthData.size());

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
            String newTagName = tagMapping.getOrDefault(data.getTagName(), "Unknown Tag");
            data.setTagName(newTagName);
            return data;
        }).collect(Collectors.toList());
    }

    // ✅ Tag Name Mappings for Each Database
    private Map<String, String> getPrimaryTagMapping() {
        Map<String, String> map = new HashMap<>();
        map.put("[LT_Panel1]MFM02A", "DG2 Cooling Coil Current");
        map.put("[LT_Panel1]MFM02KW", "DG2 Cooling Coil KW");
        map.put("[LT_Panel1]MFM02VLL", "DG2 Cooling Coil LL Voltage");
        map.put("[LT_Panel1]MFM02KWH", "DG2 Cooling Coil KWh");
        map.put("[LT_Panel1]MFM03A", "MLDB Current");
        map.put("[LT_Panel1]MFM03KW", "MLDB KW");
        map.put("[LT_Panel1]MFM03KWH", "MLDB LL Voltage");
        map.put("[LT_Panel1]MFM03VLL", "MLDB KWh");
        map.put("[LT_Panel1]MFM04A", "Fire Hydrant Current");
        map.put("[LT_Panel1]MFM04KW", "Fire Hydrant KW");
        map.put("[LT_Panel1]MFM04KWH", "Fire Hydrant LL Voltage");
        map.put("[LT_Panel1]MFM04VLL", "Fire Hydrant KWh");
        map.put("[LT_Panel1]MFM05A", "IC TR1 Current");
        map.put("[LT_Panel1]MFM05KW", "IC TR1 KW");
        map.put("[LT_Panel1]MFM05KWH", "IC TR1 LL Voltage");
        map.put("[LT_Panel1]MFM05VLL", "IC TR1 KWh");
        map.put("[LT_Panel1]MFM06A", "DG IC A Current");
        map.put("[LT_Panel1]MFM06KW", "DG IC A KW");
        map.put("[LT_Panel1]MFM06KWH", "DG IC A LL Voltage");
        map.put("[LT_Panel1]MFM06VLL", "DG IC A KWh");
        map.put("[LT_Panel1]MFM07A", "Rasin Main 2B Current");
        map.put("[LT_Panel1]MFM07KW", "Rasin Main 2B KW");
        map.put("[LT_Panel1]MFM07KWH", "Rasin Main 2B LL Voltage");
        map.put("[LT_Panel1]MFM07VLL", "Rasin Main 2B KWh");
        map.put("[LT_Panel1]MFM08A", "Rasin Main 1B Current");
        map.put("[LT_Panel1]MFM08KW", "Rasin Main 1B KW");
        map.put("[LT_Panel1]MFM08", "Rasin Main 1B LL Voltage");
        map.put("[LT_Panel1]MFM08VLL", "Rasin Main 1B KWh");
        map.put("[LT_Panel1]MFM09A", "MLDB Current");
        map.put("[LT_Panel1]MFM09KW", "MLDB KW");
        map.put("[LT_Panel1]MFM09KWH", "MLDB LL Voltage");
        map.put("[LT_Panel1]MFM09VLL", "MLDB KWh");
        map.put("[LT_Panel1]MFM10A", "DG IC B Current");
        map.put("[LT_Panel1]MFM10KW", "DG IC B KW");
        map.put("[LT_Panel1]MFM10KWH", "DG IC B LL Voltage");
        map.put("[LT_Panel1]MFM10VLL", "DG IC B KWh");
        map.put("[LT_Panel1]MFM11A", "Rasin Main 2A Current");
        map.put("[LT_Panel1]MFM11KW", "Rasin Main 2A KW");
        map.put("[LT_Panel1]MFM11KWH", "Rasin Main 2A LL Voltage");
        map.put("[LT_Panel1]MFM11VLL", "Rasin Main 2A KWh");
        
        return map;
    }

    private Map<String, String> getSecondaryTagMapping() {
        Map<String, String> map = new HashMap<>();
        map.put("[LT_Panel1]MFM12A", "4F LT 3A Current");
        map.put("[LT_Panel1]MFM12KW", "4F LT 3A KW");
        map.put("[LT_Panel1]MFM12KWH", "4F LT 3A KWh");
        map.put("[LT_Panel1]MFM12VLL", "4F LT 3A LL Voltage");
        map.put("[LT_Panel1]MFM13A", "IC TR4 Current");
        map.put("[LT_Panel1]MFM13KW", "IC TR4  KW");
        map.put("[LT_Panel1]MFM13KWH", "IC TR4  KWh");
        map.put("[LT_Panel1]MFM13VLL", "IC TR4  LL Voltage");
        map.put("[LT_Panel1]MFM14A", "Rasin Main 3A Current");
        map.put("[LT_Panel1]MFM14KW", "Rasin Main 3A  KW");
        map.put("[LT_Panel1]MFM14KWH", "Rasin Main 3A  KWh");
        map.put("[LT_Panel1]MFM14VLL", "Rasin Main 3A  LL Voltage");
        map.put("[LT_Panel1]MFM15A", "7F A2H LTP3A Current");
        map.put("[LT_Panel1]MFM15KW", "7F A2H LTP3A  KW");
        map.put("[LT_Panel1]MFM15KWH", "7F A2H LTP3A  KWh");
        map.put("[LT_Panel1]MFM15VLL", "7F A2H LTP3A  LL Voltage");
        map.put("[LT_Panel1]MFM16A", "IC TR3 Current");
        map.put("[LT_Panel1]MFM16KW", "IC TR3  KW");
        map.put("[LT_Panel1]MFM16KWH", "IC TR3  KWh");
        map.put("[LT_Panel1]MFM16VLL", "IC TR3  LL Voltage");
        map.put("[LT_Panel1]MFM17A", "DG IC C Current");
        map.put("[LT_Panel1]MFM17KW", "DG IC C  KW");
        map.put("[LT_Panel1]MFM17KWH", "DG IC C  KWh");
        map.put("[LT_Panel1]MFM17VLL", "DG IC C  LL Voltage");
        map.put("[LT_Panel1]MFM18A", "Rasin Main 4A Current");
        map.put("[LT_Panel1]MFM18KW", "Rasin Main 4A  KW");
        map.put("[LT_Panel1]MFM18KWH", "Rasin Main 4A  KWh");
        map.put("[LT_Panel1]MFM18VLL", "Rasin Main 4A  LL Voltage");
        map.put("[LT_Panel1]MFM19A", "Nxtra RMG Current");
        map.put("[LT_Panel1]MFM19KW", "Nxtra RMG  KW");
        map.put("[LT_Panel1]MFM19KWH", "Nxtra RMG  KWh");
        map.put("[[LT_Panel1]MFM19VLL", "Nxtra RMG  LL Voltage");
        return map;
    }

    private Map<String, String> getThirdTagMapping() {
        Map<String, String> map = new HashMap<>();
        map.put("[7VCB]MFM09_A", "LTP 3B Current");
        map.put("[7VCB]MFM09_KW", "LTP 3B KW");
        map.put("[7VCB]MFM09_VLL", "LTP 3B LL Voltage");
        map.put("[7VCB]MFM09_WH", "LTP 3B KWh");
        map.put("[7VCB]MFM10_A", "Rasin Main 4B Current");
        map.put("[7VCB]MFM10_KW", "Rasin Main 4B KW");
        map.put("[7VCB]MFM10_VLL", "Rasin Main 4B Voltage");
        map.put("[7VCB]MFM10_WH", "Rasin Main 4B KWh");
        map.put("[7VCB]MFM11_A", "I/C TR6 Current");
        map.put("[7VCB]MFM11_KW", "I/C TR6  KW");
        map.put("[7VCB]MFM11_VLL", "I/C TR6  Voltage");
        map.put("[7VCB]MFM11_WH", "I/C TR6  KWh");
        map.put("[7VCB]MFM12_A", "DG I/C D Current");
        map.put("[7VCB]MFM12_KW", "DG I/C D  KW");
        map.put("[7VCB]MFM12_VLL", "DG I/C D  Voltage");
        map.put("[7VCB]MFM12_WH", "DG I/C D  KWh");
        map.put("[7VCB]MFM13_A", "Rasin Main 3B Current");
        map.put("[7VCB]MFM13_KW", "Rasin Main 3B KW");
        map.put("[7VCB]MFM13_VLL", "Rasin Main 3B Voltage");
        map.put("[7VCB]MFM13_WH", "Rasin Main 3B KWh");
        map.put("[7VCB]MFM14_A", "I/C TR5 Current");
        map.put("[7VCB]MFM14_KW", "I/C TR5  KW");
        map.put("[7VCB]MFM14_VLL", "I/C TR5  Voltage");
        map.put("[7VCB]MFM14_WH", "I/C TR5  KWh");
        map.put("[7VCB]MFM15_A", "Rasin Main 5A Current");
        map.put("[7VCB]MFM15_KW", "Rasin Main 5A KW");
        map.put("[7VCB]MFM15_VLL", "Rasin Main 5A Voltage");
        map.put("[7VCB]MFM15_WH", "Rasin Main 5A KWh");
    
        return map;
    }

    private Map<String, String> getFourthTagMapping() {
        Map<String, String> map = new HashMap<>();
        map.put("[7VCB]MFM02_VLL", "Nxtra IC LL Voltage");
        map.put("[7VCB]MFM02_A", "Nxtra IC Current");
        map.put("[7VCB]MFM02_KW", "Nxtra IC KW");
        map.put("[7VCB]MFM02_WH", "Nxtra IC KWh");
        map.put("[7VCB]MFM05_VLL", "Nxtra TR3 LL Voltage");
        map.put("[7VCB]MFM05_A", "Nxtra TR3 Current");
        map.put("[7VCB]MFM05_KW", "Nxtra TR3 KW");
        map.put("[7VCB]MFM05_WH", "Nxtra TR3 KWh");
        map.put("[7VCB]MFM06_VLL", "Nxtra TR4 LL Voltage");
        map.put("[7VCB]MFM06_A", "Nxtra TR4 Current");
        map.put("[7VCB]MFM06_KW", "Nxtra TR4 KW");
        map.put("[7VCB]MFM06_WH", "Nxtra TR4 K");
        map.put("[7VCB]MFM07_VLL", "Nxtra TR5 LL Voltage");
        map.put("[7VCB]MFM07_A", "Nxtra TR5 Current");
        map.put("[7VCB]MFM07_KW", "Nxtra TR5 KW");
        map.put("[7VCB]MFM07_WH", "Nxtra TR5 KWh");
        map.put("[7VCB]MFM08_VLL", "Nxtra TR6 LL Voltage");
        map.put("[7VCB]MFM08_A", "Nxtra TR6 Current");
        map.put("[7VCB]MFM08_KW", "Nxtra TR6 KW");
        map.put("[7VCB]MFM08_WH", "Nxtra TR6 KWh");
       
        return map;
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

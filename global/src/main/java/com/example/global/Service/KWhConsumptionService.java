package com.example.global.Service;

import com.example.global.DAO.KWhConsumptionDAO;
import com.example.global.Repository.fourth.FourthRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KWhConsumptionService {

    // @Autowired
    // private PrimaryRepository primaryRepository;

    // @Autowired
    // private SecondaryRepository secondaryRepository;

    // @Autowired
    // private ThirdRepository thirdRepository;

    @Autowired
    private FourthRepository fourthRepository;



     public List<KWhConsumptionDAO> getConsumptionForDate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
          
         endDateTime = endDateTime.withSecond(59).withNano(999_000_000);
        List<KWhConsumptionDAO> result = new ArrayList<>();

        // Fetch data from all repositories
        // List<KWhConsumptionDAO> primaryData = primaryRepository.findConsumptionData(startDateTime, endDateTime);
        // List<KWhConsumptionDAO> secondaryData = secondaryRepository.findConsumptionData(startDateTime, endDateTime);
        // List<KWhConsumptionDAO> thirdData = thirdRepository.findConsumptionData(startDateTime, endDateTime);
        List<KWhConsumptionDAO> fourthData = fourthRepository.findConsumptionData(startDateTime, endDateTime);
       
        // Modify Tag Names using TagMappingService
    //    List<KWhConsumptionDAO> modifiedPrimary = filterValidData(modifyTagNames(primaryData, getPrimaryTagMapping()));
        // List<KWhConsumptionDAO> modifiedSecondary = filterValidData(modifyTagNames(secondaryData, getSecondaryTagMapping()));
        // List<KWhConsumptionDAO> modifiedThird = filterValidData(modifyTagNames(thirdData, getThirdTagMapping()));
        List<KWhConsumptionDAO> modifiedFourth = filterValidData(modifyTagNames(fourthData, getFourthTagMapping()));

      
       
        // Add modified data to result
        // result.addAll(modifiedPrimary);
        // result.addAll(modifiedSecondary);
        // result.addAll(thirdData);
        result.addAll(modifiedFourth);
      
        return result;
    }

    private List<KWhConsumptionDAO> modifyTagNames(List<KWhConsumptionDAO> dataList, Map<String, String> tagMapping) {
    return dataList.stream()
        .map(data -> {
            String newTagName = tagMapping.getOrDefault(data.getTagName().trim(), data.getTagName());
            Timestamp startTs = (data.getStartDateTime() != null) ? Timestamp.valueOf(data.getStartDateTime()) : null;
            Timestamp endTs = (data.getEndDateTime() != null) ? Timestamp.valueOf(data.getEndDateTime()) : null;
            return new KWhConsumptionDAO(
                data.getDatabaseName(),
                newTagName,
                startTs,
                endTs,
                data.getStartValue(),
                data.getEndValue()
            );
        })
        .collect(Collectors.toList());
}

    private List<KWhConsumptionDAO> filterValidData(List<KWhConsumptionDAO> dataList) {
        return dataList.stream()
                .filter(data -> data.getStartDateTime() != null && data.getEndDateTime() != null) // Ensure both datetime values exist
                .collect(Collectors.toList());
    }

public Workbook exportConsumptionData(LocalDateTime startDateTime, LocalDateTime endDateTime) throws IOException {
     endDateTime = endDateTime.withSecond(59).withNano(999_000_000);
    // Get data from both repositories
    List<KWhConsumptionDAO> fourthData = fourthRepository.findConsumptionData(startDateTime, endDateTime);
  
    List<KWhConsumptionDAO> allData = new ArrayList<>();
    allData.addAll(fourthData);
  

    // Load Excel template from resources
    InputStream templateStream = new ClassPathResource("templates/template.xlsx").getInputStream();
    Workbook workbook = new XSSFWorkbook(templateStream);

    // Load tag mappings
    Map<String, String> primaryTagMapping = getPrimaryTagMapping();
    Map<String, String> secondaryTagMapping = getSecondaryTagMapping();

    // Map of tag name to sheet name and row 
    Map<String, SheetMapping> tagToSheetMapping = new HashMap<>();
    tagToSheetMapping.put("[LT_Panel1]N2MFM09KWH", new SheetMapping("Nxtra SS", 2));
    tagToSheetMapping.put("[7VCB]MFM02WH", new SheetMapping("Nxtra SS", 3));
    tagToSheetMapping.put("[7VCB]MFM03WH", new SheetMapping("Nxtra SS", 4));
    tagToSheetMapping.put("[7VCB]MFM04WH", new SheetMapping("Nxtra SS", 5));
    tagToSheetMapping.put("[7VCB]MFM05WH", new SheetMapping("Nxtra SS", 6));
    tagToSheetMapping.put("[7VCB]MFM06WH", new SheetMapping("Nxtra SS", 7));
    tagToSheetMapping.put("[7VCB]MFM07WH", new SheetMapping("Nxtra SS", 8));
    tagToSheetMapping.put("[7VCB]MFM08WH", new SheetMapping("Nxtra SS", 9));
    tagToSheetMapping.put("[7VCB]MFM11WH", new SheetMapping("Nxtra SS", 10));
    tagToSheetMapping.put("[7VCB]MFM09WH", new SheetMapping("Nxtra SS", 11));
    tagToSheetMapping.put("[7VCB]MFM10WH", new SheetMapping("Nxtra SS", 12));
    tagToSheetMapping.put("[7VCB]MFM14WH", new SheetMapping("Nxtra SS", 13));
    tagToSheetMapping.put("[7VCB]MFM15WH", new SheetMapping("Nxtra SS", 14));
    tagToSheetMapping.put("[7VCB]MFM13WH", new SheetMapping("Nxtra SS", 15));
    tagToSheetMapping.put("[7VCB]MFM12WH", new SheetMapping("Nxtra SS", 16));
    tagToSheetMapping.put("[LT_Panel1]N1MFM15KWH", new SheetMapping("Nxtra SS", 17));
    tagToSheetMapping.put("[LT_Panel1]N1MFM02KWH", new SheetMapping("Nxtra SS", 18));
    tagToSheetMapping.put("[LT_Panel1]N1MFM12KWH", new SheetMapping("Nxtra SS", 19));
    tagToSheetMapping.put("[LT_Panel1]N1MFM03KWH", new SheetMapping("Nxtra SS", 20));
    tagToSheetMapping.put("[LT_Panel1]N1MFM04KWH", new SheetMapping("Nxtra SS", 21));
    tagToSheetMapping.put("[LT_Panel1]N1MFM14KWH", new SheetMapping("Nxtra SS", 22));
    tagToSheetMapping.put("[LT_Panel1]N1MFM16KWH", new SheetMapping("Nxtra SS", 23));
    tagToSheetMapping.put("[LT_Panel1]N1MFM05KWH", new SheetMapping("Nxtra SS", 24));
    tagToSheetMapping.put("[LT_Panel1]N1MFM06KWH", new SheetMapping("Nxtra SS", 25));
    tagToSheetMapping.put("[LT_Panel1]N1MFM07KWH", new SheetMapping("Nxtra SS", 26));
    tagToSheetMapping.put("[LT_Panel1]N1MFM08KWH", new SheetMapping("Nxtra SS", 27));
    tagToSheetMapping.put("[LT_Panel1]N1MFM30KWH", new SheetMapping("Nxtra SS", 28));
    tagToSheetMapping.put("[LT_Panel1]N1MFM09KWH", new SheetMapping("Nxtra SS", 29));
    tagToSheetMapping.put("[LT_Panel1]N1MFM19KWH", new SheetMapping("Nxtra SS", 30));
    tagToSheetMapping.put("[LT_Panel1]N1MFM17KWH", new SheetMapping("Nxtra SS", 31));
    tagToSheetMapping.put("[LT_Panel1]N1MFM18KWH", new SheetMapping("Nxtra SS", 32));
    tagToSheetMapping.put("[LT_Panel1]N1MFM20KWH", new SheetMapping("Nxtra SS", 33));
    tagToSheetMapping.put("[LT_Panel1]N1MFM10KWH", new SheetMapping("Nxtra SS", 34));
    tagToSheetMapping.put("[LT_Panel1]N1MFM11KWH", new SheetMapping("Nxtra SS", 35));
    tagToSheetMapping.put("[LT_Panel1]N1MFM13KWH", new SheetMapping("Nxtra SS", 36));
    tagToSheetMapping.put("[LT_Panel1]N2MFM04KWH", new SheetMapping("Nxtra SS", 37));
    tagToSheetMapping.put("[LT_Panel1]N2MFM03KWH", new SheetMapping("Nxtra SS", 38));
    tagToSheetMapping.put("[LT_Panel1]N2MFM05KWH", new SheetMapping("Nxtra SS", 39));
    tagToSheetMapping.put("[LT_Panel1]N2MFM06KWH", new SheetMapping("Nxtra SS", 40));
    tagToSheetMapping.put("[LT_Panel1]N2MFM07KWH", new SheetMapping("Nxtra SS", 41));
    tagToSheetMapping.put("[LT_Panel1]N2MFM08KWH", new SheetMapping("Nxtra SS", 42));
    tagToSheetMapping.put("[LT_Panel1]N2MFM02KWH", new SheetMapping("Nxtra SS", 43));
    
    tagToSheetMapping.put("[LT_Panel1]N1MFM21KWH", new SheetMapping("DG Sync Panel", 2)); // Row 3
    tagToSheetMapping.put("[LT_Panel1]N1MFM29KWH", new SheetMapping("DG Sync Panel", 3));
    tagToSheetMapping.put("[LT_Panel1]N1MFM25KWH", new SheetMapping("DG Sync Panel", 4));
    tagToSheetMapping.put("[LT_Panel1]N1MFM28KWH", new SheetMapping("DG Sync Panel", 5));
    tagToSheetMapping.put("[LT_Panel1]N1MFM24KWH", new SheetMapping("DG Sync Panel", 6));
    tagToSheetMapping.put("[LT_Panel1]N1MFM26KWH", new SheetMapping("DG Sync Panel", 7));
    tagToSheetMapping.put("[LT_Panel1]N1MFM22KWH", new SheetMapping("DG Sync Panel", 8));
    tagToSheetMapping.put("[LT_Panel1]N1MFM27KWH", new SheetMapping("DG Sync Panel", 9));
    tagToSheetMapping.put("[LT_Panel1]N1MFM23KWH", new SheetMapping("DG Sync Panel", 10));

    tagToSheetMapping.put("[LT_Panel1]N2MFM10KWH", new SheetMapping("Airtel SS", 2)); // Row 3
    tagToSheetMapping.put("[LT_Panel1]N2MFM11KWH", new SheetMapping("Airtel SS", 3));
    tagToSheetMapping.put("[LT_Panel1]N2MFM12KWH", new SheetMapping("Airtel SS", 4));
    tagToSheetMapping.put("[LT_Panel1]N2MFM13KWH", new SheetMapping("Airtel SS", 5));
    tagToSheetMapping.put("[LT_Panel_1A]TR1_KWH", new SheetMapping("Airtel SS", 6));
    tagToSheetMapping.put("[LT_Panel_1A]DG1_KWH", new SheetMapping("Airtel SS", 7));
    tagToSheetMapping.put("[LT_Panel_1A]TR2_KWH", new SheetMapping("Airtel SS", 8));
    tagToSheetMapping.put("[LT_Panel_1A]DG2_KWH", new SheetMapping("Airtel SS", 9)); 
    tagToSheetMapping.put("PCC1_Total_KWH", new SheetMapping("Airtel SS", 10));
    tagToSheetMapping.put("PCC2_Total_KWH", new SheetMapping("Airtel SS", 11));


CellStyle borderedStyle = workbook.createCellStyle();
borderedStyle.setBorderTop(BorderStyle.THIN);
borderedStyle.setBorderBottom(BorderStyle.THIN);
borderedStyle.setBorderLeft(BorderStyle.THIN);
borderedStyle.setBorderRight(BorderStyle.THIN);
borderedStyle.setAlignment(HorizontalAlignment.CENTER);
borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER);

DataFormat format = workbook.createDataFormat();
borderedStyle.setDataFormat(format.getFormat("0.00")); // 2 decimal format

    

   for (KWhConsumptionDAO dao : allData) {
    String tag = dao.getTagName();

    SheetMapping mapping = tagToSheetMapping.get(tag);
    if (mapping == null) {
        System.out.println("No mapping for tag: " + tag);
        continue;
    }

    Sheet sheet = workbook.getSheet(mapping.sheetName);
    if (sheet == null) {
        System.out.println("No sheet found: " + mapping.sheetName);
        continue;
    }

    Row row = sheet.getRow(mapping.rowIndex);
    if (row == null) row = sheet.createRow(mapping.rowIndex);

    String feederDetail = primaryTagMapping.getOrDefault(tag,
                           secondaryTagMapping.getOrDefault(tag, "Unknown Feeder"));

    // // Write feeder detail in column B (index 1) for example
    // Cell feederCell = row.getCell(6);
    // if (feederCell == null) feederCell = row.createCell(6);
    // feederCell.setCellValue(feederDetail);

    double startVal = dao.getStartValue();
    double endVal = dao.getEndValue();
    double consumption = dao.getConsumption();

    System.out.println("Writing for tag: " + tag + " feeder: " + feederDetail
        + " startVal: " + startVal + " endVal: " + endVal + " consumption: " + consumption);

createBorderedCell(row, 2, dao.getStartValue(), borderedStyle);
createBorderedCell(row, 3, dao.getEndValue(), borderedStyle);
createBorderedCell(row, 4, dao.getConsumption(), borderedStyle);


}

    return workbook; // Return filled workbook object
}

private void createBorderedCell(Row row, int colIndex, double value, CellStyle style) {
    Cell cell = row.getCell(colIndex);
    if (cell == null) {
        cell = row.createCell(colIndex);
    }
    cell.setCellStyle(style);
    cell.setCellValue(Math.round(value * 100.0) / 100.0); // round to 2 decimals
}


    private static class SheetMapping {
        String sheetName;
        int rowIndex;

        SheetMapping(String sheetName, int rowIndex) {
            this.sheetName = sheetName;
            this.rowIndex = rowIndex;
        }
    }


    
    public Map<String, String> getPrimaryTagMapping() {
        Map<String, String> map = new HashMap<>();
        
       
        return map;
    }

    public Map<String, String> getSecondaryTagMapping() {
        Map<String, String> map = new HashMap<>();
                 
        return map;
    }
    public Map<String, String> getFourthTagMapping(){
        Map<String, String> map = new HashMap<>();
        map.put("[LT_Panel1]N2MFM09KWH", "Nxtra Single VCB-Incomer");
        map.put("[7VCB]MFM02WH", "7VCB-Incomer");
        map.put("[7VCB]MFM03WH", "7VCB-Spare Tranformer Feeder");
        map.put("[7VCB]MFM04WH", "7VCB-Transformer-7");
        map.put("[7VCB]MFM05WH", "7VCB-Transformer-3");
        map.put("[7VCB]MFM06WH", "7VCB-Transformer-4");
        map.put("[7VCB]MFM07WH", "7VCB-Transformer-5");
        map.put("[7VCB]MFM08WH", "7VCB-Transformer-6"); 
        map.put("[7VCB]MFM11WH", "LTP3-Transformer-6");
        map.put("[7VCB]MFM14WH", "LTP3-Transformer-5");
        map.put("[7VCB]MFM12WH", "LTP3-DG IC-D");
        map.put("[7VCB]MFM09WH", "LTP3-OG To 7FL LTPanel 3B");
        map.put("[7VCB]MFM10WH", "LTP3-OG To RM-4B");
        map.put("[7VCB]MFM13WH", "LTP3-OG To RM-3B");
        map.put("[7VCB]MFM15WH", "LTP3-OG To RM-5A");
        map.put("[LT_Panel1]N1MFM15KWH", "LTP1-Transformer-7");
        map.put("[LT_Panel1]N1MFM30KWH", "LTP1-IC From Transformer-2");
        map.put("[LT_Panel1]N1MFM05KWH", "LTP1-IC From Transformer-1");
        map.put("[LT_Panel1]N1MFM12KWH", "LTP1-OG To GF Smoke Exhaust Panel-A");
        map.put("[LT_Panel1]N1MFM06KWH", "LTP1-DG IC-A");
        map.put("[LT_Panel1]N1MFM10KWH", "LTP1-DG IC-B");
        map.put("[LT_Panel1]N1MFM02KWH", "LTP1-OG GF Freshair Panel-A");
        map.put("[LT_Panel1]N1MFM03KWH", "LTP1-OG To GF MLDB Panel");
        map.put("[LT_Panel1]N1MFM04KWH", "LTP1-OG TO Fire Hydrant Panel");
        map.put("[LT_Panel1]N1MFM07KWH", "LTP1-OG To RM-2B");
        map.put("[LT_Panel1]N1MFM08KWH", "LTP1-OG To RM-1B");
        map.put("[LT_Panel1]N1MFM09KWH", "LTP1-OG To MPDB Panel");
        map.put("[LT_Panel1]N1MFM19KWH", "LTP1-OG To Spare");
        map.put("[LT_Panel1]N1MFM17KWH", "LTP1-OG To GF Freshair Panel-B");
        map.put("[LT_Panel1]N1MFM18KWH", "LTP1-OG To GF Smoke Exhaust Panel-B");
        map.put("[LT_Panel1]N1MFM20KWH", "LTP1-Spare Feeder 1600A (16F)");
        map.put("[LT_Panel1]N1MFM11KWH", "LTP1-OG To RM-2A");
        map.put("[LT_Panel1]N1MFM13KWH", "LTP1-OG To RM-1A");
        map.put("[LT_Panel1]N1MFM14KWH", "LTP1-Spare (2F3)");
        map.put("[LT_Panel1]N1MFM16KWH", "LTP1-Spare Feeder 1600A (4F)");
        map.put("[LT_Panel1]N2MFM03KWH", "LTP2-IC From Transformer-4");
        map.put("[LT_Panel1]N2MFM06KWH", "LTP2-IC From Transformer-3");
        map.put("[LT_Panel1]N2MFM07KWH", "LTP2-DG IC-C");
        map.put("[LT_Panel1]N2MFM02KWH", "LTP2-OG To 4F LTPanel-3A");
        map.put("[LT_Panel1]N2MFM04KWH", "LTP2-OG To RM-3A");
        map.put("[LT_Panel1]N2MFM08KWH", "LTP2-OG To RM-4A");
        map.put("[LT_Panel1]N2MFM05KWH", "LTP2-OG To 7FL LTPanel-3A");

        map.put("[LT_Panel1]N2MFM10KWH", "Airtel Singe VCB - Incomer");
        map.put("[LT_Panel1]N2MFM11KWH", "Airte 3VCB - Incomer");
        map.put("[LT_Panel1]N2MFM12KWH", "Airtel 3VCB-OG To Transforment1");
        map.put("[LT_Panel1]N2MFM13KWH", "Airtel 3VCB-OG To Transforment2");
        map.put("[LT_Panel1]N1MFM15KWH", "DG Sync-IC From DG1");
        map.put("[LT_Panel1]N1MFM29KWH", "DG Sync-DG6 PCC");
        map.put("[LT_Panel1]N1MFM28KWH", "DG Sync-DG5 PCC");
        map.put("[LT_Panel1]N1MFM26KWH", "DG Sync-DG3 PCC");
        map.put("[LT_Panel1]N1MFM27KWH", "DG Sync-DG4 PCC");
        map.put("[LT_Panel1]N1MFM21KWH", "DG Sync-IC From DG1");
        map.put("[LT_Panel1]N1MFM25KWH", "DG Sync-IC From DG6");
        map.put("[LT_Panel1]N1MFM24KWH", "DG Sync-IC From DG5");
        map.put("[LT_Panel1]N1MFM22KWH", "DG Sync-IC From DG3");
        map.put("[LT_Panel1]N1MFM23KWH", "DG Sync-IC From DG4");
        map.put("[LT_Panel_1A]TR1_KWH", "LTP1A-IC From Transformer1");
        map.put("[LT_Panel_1A]DG1_KWH", "LTP1A-IC From DG1");
        map.put("[LT_Panel_1A]TR2_KWH", "LTP1B-IC From Transformer2");
        map.put("[LT_Panel_1A]DG2_KWH", "LTP1B-IC From DG2");
        map.put("PCC1_Total_KWH", "DG1 PCC");
        map.put("PCC2_Total_KWH", "DG2 PCC");
        

         
        return map;
    }
    
    }


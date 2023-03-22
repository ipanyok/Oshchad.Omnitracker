package ua.datastech.omnitracker.service.parse;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelFileReader {

    @SneakyThrows
    public static void read(String fileLocation) {
        FileInputStream file = new FileInputStream(new File(fileLocation));
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {
            data.put(i, new ArrayList<>());
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING:
                        data.get(new Integer(i)).add(cell.getRichStringCellValue().getString());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            data.get(i).add(cell.getDateCellValue() + "");
                        ***REMOVED*** else {
                            data.get(i).add(cell.getNumericCellValue() + "");
                        ***REMOVED***
                        break;
                    case BOOLEAN:
                        data.get(i).add(cell.getBooleanCellValue() + "");
                        break;
                    case FORMULA:
                        data.get(i).add(cell.getCellFormula() + "");
                        break;
                    default:
                        data.get(new Integer(i)).add(" ");
                ***REMOVED***
            ***REMOVED***
            i++;
        ***REMOVED***
        List<String> logins = new ArrayList<>();
        for (int row = 2; row < sheet.getPhysicalNumberOfRows(); row++) {
            Cell cell = sheet.getRow(row).getCell(2);
            if (cell != null && cell.getStringCellValue() != null && !cell.getStringCellValue().equals("")) {
                logins.add(cell.getStringCellValue());
            ***REMOVED***
        ***REMOVED***
        System.out.println(logins);
//        data.entrySet().stream()
//                .filter(integerListEntry -> integerListEntry.getValue().size() >= 2)
//                .forEach(entry -> System.out.println(entry.getValue().get(2)));
    ***REMOVED***

    public static void main(String[] args) {
        read("d:***REMOVED******REMOVED***test_file.xlsx");
    ***REMOVED***


***REMOVED***

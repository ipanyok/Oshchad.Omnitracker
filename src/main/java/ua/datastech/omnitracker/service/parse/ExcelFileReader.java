package ua.datastech.omnitracker.service.parse;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ExcelFileReader {

    @SneakyThrows
    public static List<String> read(String attachment) {
        String attachmentWithoutWrongData = attachment.replaceAll("***REMOVED***r***REMOVED***n", "");
        byte[] decode = Base64.getDecoder().decode(attachmentWithoutWrongData.getBytes(StandardCharsets.UTF_8));
        InputStream input = new ByteArrayInputStream(decode);

        Workbook workbook = new XSSFWorkbook(input);
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
        // todo change row/cell for prod excel
        for (int row = 6; row < sheet.getPhysicalNumberOfRows(); row++) {
            Cell cell = sheet.getRow(row).getCell(3);
            if (cell != null && cell.getStringCellValue() != null && !cell.getStringCellValue().equals("")) {
                logins.add(cell.getStringCellValue());
            ***REMOVED***
        ***REMOVED***

        return logins;
    ***REMOVED***

***REMOVED***

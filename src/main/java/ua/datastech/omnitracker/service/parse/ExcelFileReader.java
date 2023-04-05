package ua.datastech.omnitracker.service.parse;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class ExcelFileReader {

    private final static int ROW_NUMBER = 2;
    private final static int CELL_NUMBER = 2;
    private final static int SHEET_NUMBER = 0;

    @SneakyThrows
    public static List<String> read(String attachment) {
        String attachmentWithoutWrongData = attachment.replaceAll("***REMOVED***r***REMOVED***n", "");
        byte[] decode = Base64.getDecoder().decode(attachmentWithoutWrongData.getBytes(StandardCharsets.UTF_8));
        InputStream input = new ByteArrayInputStream(decode);

        Workbook workbook = new XSSFWorkbook(input);
        Sheet sheet = workbook.getSheetAt(SHEET_NUMBER);

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

        for (int row = ROW_NUMBER; row < sheet.getPhysicalNumberOfRows(); row++) {
            Cell cell = null;
            try {
                if (sheet != null && sheet.getRow(row) != null) {
                    cell = sheet.getRow(row).getCell(CELL_NUMBER);
                    if (cell != null && cell.getStringCellValue() != null && !cell.getStringCellValue().equals("")) {
                        logins.add(cell.getStringCellValue());
                    ***REMOVED***
                ***REMOVED***
            ***REMOVED*** catch (Exception e) {
                log.error(String.format("Can't parse row %s, cell %s: ", row, cell) + e.getMessage());
            ***REMOVED***
        ***REMOVED***

        return logins;
    ***REMOVED***

***REMOVED***

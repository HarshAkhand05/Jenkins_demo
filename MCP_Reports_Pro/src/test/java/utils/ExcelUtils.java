package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    public static List<String[]> getLoginData() throws Exception {

        List<String[]> data = new ArrayList<>();

        // Path to excel file
        String path = "src/test/resources/testdata/LoginData.xlsx";

        FileInputStream fis = new FileInputStream(path);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        // i=1 means skip header row
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);

            String username = row.getCell(0).getStringCellValue();
            String password = row.getCell(1).getStringCellValue();
            String expected = row.getCell(2).getStringCellValue();

            data.add(new String[]{username, password, expected});
        }

        workbook.close();
        return data;
    }
}
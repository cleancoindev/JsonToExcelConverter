import com.opencsv.CSVReader;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class FileConverter {

    static String xlsxFileName = "language.xls";

    static void convertCSVToXLS(String csvName) throws IOException {
        File csvFile = new File(csvName);
        File xlsxFile = new File(xlsxFileName);
        if (csvFile.getName().split("\\.")[1].compareTo("csv") != 0) {
            System.out.println("File is not csv!");
            return;
        }

        CSVReader reader = new CSVReader(new FileReader(csvFile));

        String[] nextLine;
        int lineNumber = 0;

        HSSFWorkbook workbook = new HSSFWorkbook(); //create a blank workbook object
        HSSFSheet sheet = workbook.createSheet("translations");

        List<String[]> excelData = new ArrayList<>();
        while ((nextLine = reader.readNext()) != null) {
            lineNumber++;
            excelData.add(nextLine);
        }

        int rowNumber = 0;
        for (String[] row : excelData) {
            Row sheetRow = sheet.createRow(rowNumber);
            List<String> cells = new ArrayList<>();
            cells.addAll(Arrays.asList(row));
            int cellNumber = 0;
            rowNumber++;
            for (String cell : cells) {
                Cell sheetCell = sheetRow.createCell(cellNumber);
                sheetCell.setCellValue(cell);
                cellNumber++;
            }
        }

        FileOutputStream outputStream = new FileOutputStream(xlsxFile);
        workbook.write(outputStream);
        outputStream.close();
    }

}

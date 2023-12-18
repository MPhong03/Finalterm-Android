package project.finalterm.quizapp.Utils;

import android.content.Context;
import android.net.Uri;

import android.util.Log;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import project.finalterm.quizapp.Data.Word;

public class ExcelUtil {

    public static ArrayList<Word> importExcelFile(InputStream inputStream, String userId) {
        ArrayList<Word> importedWords = new ArrayList<>();
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row currentRow : sheet) {
                if (currentRow.getRowNum() > 0) {
                    String title = currentRow.getCell(0).getStringCellValue();
                    String subtitle = currentRow.getCell(1).getStringCellValue();
                    String description = currentRow.getCell(2).getStringCellValue();

                    Word word = new Word(title, subtitle, description, userId);
                    importedWords.add(word);
                }
            }

            workbook.close();

        } catch (IOException e) {
            Log.e("ExcelUtil", "Error importing Excel file: " + e.getMessage());
        }
        return importedWords;
    }

    public static boolean createExcelFile(ArrayList<Word> words, OutputStream outputStream) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Words");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Title");
        headerRow.createCell(1).setCellValue("Subtitle");
        headerRow.createCell(2).setCellValue("Description");

        int rowNum = 1;
        for (Word word : words) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(word.getTitle());
            row.createCell(1).setCellValue(word.getSubtitle());
            row.createCell(2).setCellValue(word.getDescription());
        }

        try {
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
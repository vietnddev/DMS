package com.flowiee.dms.common.utils;

import com.flowiee.dms.common.utils.constants.FileExtension;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfUtils {
    public static File cloneFileToPdf(File file, String extension) throws DocumentException, IOException {
        if (ObjectUtils.isEmpty(file) || ObjectUtils.isEmpty(extension))
            return null;
        if (FileExtension.DOC.key().equals(extension)) {
            return convertDocToPdf(file);
        }
        if (FileExtension.DOCX.key().equals(extension)) {
            return convertDocxToPdf(file);
        }
        if (FileExtension.XLS.key().equals(extension)) {
            return convertXlsToPdf(file);
        }
        if (FileExtension.XLSX.key().equals(extension)) {
            return convertXlsxToPdf(file);
        }
        return null;
    }

    public static File convertDocToPdf(File docFile) throws IOException, DocumentException {
        FileInputStream fis = new FileInputStream(docFile);
        HWPFDocument doc = new HWPFDocument(fis);

        String outputFileName = getOutputFileName(docFile.getName());
        File pdfFile = new File(docFile.getParent(), outputFileName);
        FileOutputStream fos = new FileOutputStream(pdfFile);

        Document pdfDoc = new Document();
        PdfWriter.getInstance(pdfDoc, fos);
        pdfDoc.open();

        Range range = doc.getRange();
        for (int i = 0; i < range.numParagraphs(); i++) {
            org.apache.poi.hwpf.usermodel.Paragraph paragraph = range.getParagraph(i);
            pdfDoc.add(new Paragraph(paragraph.text()));
        }

        pdfDoc.close();
        fos.close();
        fis.close();

        return pdfFile;
    }

    public static File convertDocxToPdf(File inputFile) throws IOException, DocumentException {
        FileInputStream fis = new FileInputStream(inputFile);
        XWPFDocument document = new XWPFDocument(fis);

        File pdfOutput = new File(inputFile.getParent(), getOutputFileName(inputFile.getName()));
        FileOutputStream fos = new FileOutputStream(pdfOutput);

        Document pdfDoc = new Document();
        PdfWriter.getInstance(pdfDoc, fos);
        pdfDoc.open();

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            pdfDoc.add(new Paragraph(paragraph.getText()));
        }

        pdfDoc.close();
        document.close();
        fos.close();
        fis.close();

        return pdfOutput;
    }

    public static File convertXlsToPdf(File excelInput) throws IOException, DocumentException {
        FileInputStream inputStream = new FileInputStream(excelInput);
        Workbook workbook = WorkbookFactory.create(inputStream);

        File pdfOutput = new File(excelInput.getParent(), getOutputFileName(excelInput.getName()));
        FileOutputStream outputStream = new FileOutputStream(pdfOutput);

        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);

            // Check if sheet is empty or null
            if (sheet == null || sheet.getLastRowNum() == -1) {
                // Handle empty sheet case (skip or throw exception)
                continue; // Or handle as per your application's logic
            }
            int maxColumns = getMaxColumns(sheet);
            PdfPTable table = new PdfPTable(maxColumns);

            for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row != null) {
                    for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
                        Cell cell = row.getCell(colNum);
                        if (cell != null) {
                            table.addCell(cell.toString());
                        }
                    }
                }
            }

            document.add(table);
        }

        document.close();
        workbook.close();
        outputStream.close();
        inputStream.close();

        return pdfOutput;
    }

    public static File convertXlsxToPdf(File excelInput) throws IOException, DocumentException {
        FileInputStream inputStream = new FileInputStream(excelInput);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        File pdfOutput = new File(excelInput.getParent(), getOutputFileName(excelInput.getName()));
        FileOutputStream outputStream = new FileOutputStream(pdfOutput);

        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            XSSFSheet sheet = workbook.getSheetAt(i);
            PdfPTable table = new PdfPTable(getMaxColumns(sheet));

            for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                XSSFRow row = sheet.getRow(rowNum);
                if (row != null) {
                    for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
                        XSSFCell cell = row.getCell(colNum);
                        if (cell != null) {
                            table.addCell(cell.toString());
                        }
                    }
                }
            }

            document.add(table);
        }

        document.close();
        workbook.close();
        outputStream.close();
        inputStream.close();

        return pdfOutput;
    }

    private static int getMaxColumns(Sheet sheet) {
        int maxColumns = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null && row.getLastCellNum() > maxColumns) {
                maxColumns = row.getLastCellNum();
            }
        }
        return maxColumns;
    }

    private static int getMaxColumns(XSSFSheet sheet) {
        int maxColumns = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if (row != null && row.getLastCellNum() > maxColumns) {
                maxColumns = row.getLastCellNum();
            }
        }
        return maxColumns;
    }

    private static String getOutputFileName(String inputFileName) {
        int dotIndex = inputFileName.lastIndexOf('.');
        if (dotIndex != -1) {
            return inputFileName.substring(0, dotIndex) + ".pdf";
        } else {
            return inputFileName + ".pdf";
        }
    }
}
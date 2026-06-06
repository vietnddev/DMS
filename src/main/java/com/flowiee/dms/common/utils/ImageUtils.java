package com.flowiee.dms.common.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageUtils {
    public static void cloneFileToImg(File excelFile, String outputImagePath) throws IOException {
        FileInputStream inputStream = new FileInputStream(excelFile);

        Workbook workbook;
        if (excelFile.getAbsolutePath().endsWith("xlsx")) {
            workbook = new XSSFWorkbook(inputStream);  // Đọc file .xlsx
        } else if (excelFile.getAbsolutePath().endsWith("xls")) {
            workbook = new HSSFWorkbook(inputStream);  // Đọc file .xls
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }

        Sheet sheet = workbook.getSheetAt(0);  // Lấy sheet đầu tiên (có thể điều chỉnh để xử lý nhiều sheet)

        // Tạo hình ảnh từ sheet
        BufferedImage image = sheetToImage(sheet);

        // Lưu hình ảnh ra file
        File imageOutput = new File(excelFile.getParent(), getOutputFileName(excelFile.getName()));
        if (outputImagePath != null) {
            imageOutput = new File(outputImagePath);
        }
        ImageIO.write(image, "png", imageOutput);

        workbook.close();
        inputStream.close();
    }

    private static BufferedImage sheetToImage(Sheet sheet) {
        int width = 0;
        int height = 0;

        // Tính toán kích thước của hình ảnh dựa trên số lượng hàng và cột
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                height += row.getHeightInPoints();  // Tính chiều cao theo điểm
                width = Math.max(width, row.getPhysicalNumberOfCells() * 100); // Điều chỉnh chiều rộng theo cột
            }
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);  // Đặt màu nền là trắng
        g2d.fillRect(0, 0, width, height);  // Vẽ nền trắng

        g2d.setColor(Color.BLACK);  // Đặt màu văn bản là đen

        // Vẽ nội dung của sheet lên hình ảnh
        int rowHeight = 0;
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    Cell cell = row.getCell(j);
                    String cellValue = cell != null ? cell.toString() : "";  // Lấy giá trị của ô
                    g2d.drawString(cellValue, j * 100, rowHeight);  // Vẽ giá trị ô
                }
                rowHeight += row.getHeightInPoints();  // Tăng chiều cao cho hàng tiếp theo
            }
        }

        g2d.dispose();  // Đóng đối tượng đồ họa

        return image;
    }

    private static String getOutputFileName(String inputFileName) {
        int dotIndex = inputFileName.lastIndexOf('.');
        if (dotIndex != -1) {
            return inputFileName.substring(0, dotIndex) + ".png";
        } else {
            return inputFileName + ".png";
        }
    }
}
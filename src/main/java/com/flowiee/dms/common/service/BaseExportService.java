package com.flowiee.dms.common.service;

import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.storage.model.EximModel;
import com.flowiee.dms.common.utils.RequestUtils;
import com.flowiee.dms.common.utils.constants.TemplateExport;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;

@Slf4j
public abstract class BaseExportService extends BaseService implements ExportService {
    protected abstract void writeData(Object pCondition);

    protected XSSFWorkbook mvWorkbook;
    protected EximModel    mvEximModel;

    @Override
    public EximModel exportToExcel(TemplateExport templateExport, Object pCondition, boolean templateOnly) {
        try {
            mvEximModel = new EximModel(templateExport);
            if (templateOnly) {
                mvWorkbook = new XSSFWorkbook(mvEximModel.getPathSource().toFile());
            } else {
                mvWorkbook = new XSSFWorkbook(Files.copy(mvEximModel.getPathSource(), mvEximModel.getPathTarget(), StandardCopyOption.REPLACE_EXISTING).toFile());
                writeData(pCondition);
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            mvWorkbook.write(byteArrayOutputStream);
            setFileContent(byteArrayOutputStream);
            setHttpHeaders();
            mvEximModel.setResult("OK");
            return mvEximModel;
        } catch (Exception e) {
            mvEximModel.setResult("NOK");
            throw new AppException("Error when export data!", e);
        } finally {
            try {
                if (mvWorkbook != null) mvWorkbook.close();
                Files.deleteIfExists(mvEximModel.getPathTarget());
                mvEximModel.setFinishTime(LocalTime.now());
            } catch (IOException e) {
                log.error("Error when export data!", e);
            }
        }
    }

    private void setHttpHeaders() {
        mvEximModel.setHttpHeaders(RequestUtils.createHttpHeader(mvEximModel.getDefaultOutputName()));
    }

    private void setFileContent(ByteArrayOutputStream byteArrayOS) {
        ByteArrayInputStream byteArrayIS = new ByteArrayInputStream(byteArrayOS.toByteArray());
        InputStreamResource inputStreamResource = new InputStreamResource(byteArrayIS);
        mvEximModel.setContent(inputStreamResource);
    }

    protected void setBorderCell(XSSFRow pRow, int pColFrom, int pColTo) {
        if (pRow == null) return;
        for (int j = pColFrom; j <= pColTo; j++) {
            XSSFCellStyle lvCellStyle = mvWorkbook.createCellStyle();
            lvCellStyle.setBorderTop(BorderStyle.THIN);
            lvCellStyle.setBorderBottom(BorderStyle.THIN);
            lvCellStyle.setBorderLeft(BorderStyle.THIN);
            lvCellStyle.setBorderRight(BorderStyle.THIN);

            pRow.getCell(j).setCellStyle(lvCellStyle);
        }
    }
}
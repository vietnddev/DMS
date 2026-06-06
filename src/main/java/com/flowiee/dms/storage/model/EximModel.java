package com.flowiee.dms.storage.model;

import com.flowiee.dms.common.utils.FileUtils;
import com.flowiee.dms.common.utils.constants.TemplateExport;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

import java.nio.file.Path;
import java.time.LocalTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EximModel {
    LocalTime beginTime;
    LocalTime finishTime;
    Path pathSource;
    Path pathTarget;
    InputStreamResource content;
    String fileType;
    String defaultOutputName;
    HttpHeaders httpHeaders;
    String result;

    public EximModel(TemplateExport templateExport) {
        beginTime = LocalTime.now();
        long currentTime = beginTime.toNanoOfDay();
        pathSource = Path.of(FileUtils.excelTemplatePath + "/" + templateExport.getTemplateName());
        pathTarget = Path.of(FileUtils.excelTemplatePath + "/temp/" + currentTime + "_" + templateExport.getTemplateName());
        fileType = FileUtils.getFileExtension(templateExport.getTemplateName());
        defaultOutputName = currentTime + "_" + templateExport.getTemplateName();
    }
}
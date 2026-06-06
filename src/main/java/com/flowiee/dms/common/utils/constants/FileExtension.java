package com.flowiee.dms.common.utils.constants;

import lombok.Getter;

@Getter
public enum FileExtension {
    PNG("png", true),
    JPG("jpg", true),
    JPEG("jpeg", true),
    PDF("pdf", true),
    XLS("xls", true),
    XLSX("xlsx", true),
    PPT("ppt", true),
    PPTX("pptx", true),
    DOC("doc", true),
    DOCX("docx", true),
    TXT("txt", true),
    LOG("log", true),
    JSON("json", true);

    private final String key;
    private final boolean isAllowUpload;

    FileExtension(String key, boolean isAllowUpload) {
        this.key = key;
        this.isAllowUpload = isAllowUpload;
    }

    public String key() {
        return this.name().toLowerCase();
    }
}
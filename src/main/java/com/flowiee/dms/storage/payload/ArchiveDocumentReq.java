package com.flowiee.dms.storage.payload;

import lombok.Data;

@Data
public class ArchiveDocumentReq {
    private long documentId;
    private String versionName;
}
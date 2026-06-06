package com.flowiee.dms.storage.payload;

import lombok.Data;

@Data
public class RevertDocumentReq {
    private long documentId;
    private long versionId;
}
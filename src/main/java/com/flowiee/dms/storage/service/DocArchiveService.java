package com.flowiee.dms.storage.service;

import com.flowiee.dms.storage.entity.DocVersion;

import java.io.IOException;

public interface DocArchiveService {
    long getNextDocVersion(long documentId);

    long getLatestDocVersion(long documentId);

    void archiveVersion(long documentId, DocVersion docVersion) throws IOException;

    void restoreOldVersion(long documentId, long versionId);
}
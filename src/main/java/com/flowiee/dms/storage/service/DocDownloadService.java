package com.flowiee.dms.storage.service;

import com.flowiee.dms.storage.model.DownloadResource;

import java.io.IOException;

public interface DocDownloadService {
    DownloadResource download(long documentId)  throws IOException;
}
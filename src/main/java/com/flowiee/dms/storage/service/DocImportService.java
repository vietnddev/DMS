package com.flowiee.dms.storage.service;

import com.flowiee.dms.storage.dto.DocumentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocImportService {
    List<DocumentDTO> importDocuments(long parentId, MultipartFile zipFile, boolean inheritPermission) throws IOException;
}
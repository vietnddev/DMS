package com.flowiee.dms.storage.service;

import com.flowiee.dms.storage.dto.DocumentDTO;

import java.util.List;

public interface FolderTreeService {
    List<DocumentDTO> getDocumentWithTreeForm(Long docParentId, boolean isOnlyFolder);

    DocumentDTO findByDocId(long documentId);
}
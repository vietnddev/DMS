package com.flowiee.dms.storage.service;

import com.flowiee.dms.storage.entity.DocShare;
import com.flowiee.dms.storage.model.DocMetaModel;
import com.flowiee.dms.storage.model.DocShareModel;
import com.flowiee.dms.storage.dto.DocumentDTO;
import java.util.List;

public interface DocActionService {
    int DELETE_NORMAL = 0;
    int DELETE_SCHEDULE = 1;

    DocumentDTO saveDoc(DocumentDTO documentDTO);

    DocumentDTO updateDoc(DocumentDTO data, Long documentId);

    String updateMetadata(List<DocMetaModel> metaDTOs, Long documentId);

    String deleteDoc(Long documentId, boolean isDeleteSubDoc);

    String deleteDoc(Long documentId, boolean isDeleteSubDoc, boolean forceDelete, int modeDelete);

    DocumentDTO copyDoc(Long docId, Long destinationId, String nameCopy);

    String moveDoc(Long docId, Long destinationId);

    List<DocShare> shareDoc(Long docId, List<DocShareModel> accountShares, boolean applyForSubFolder);

    void restoreTrash(long documentId);
}
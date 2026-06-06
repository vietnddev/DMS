package com.flowiee.dms.storage.service;

import com.flowiee.dms.storage.entity.Document;
import com.flowiee.dms.storage.model.DocMetaModel;
import com.flowiee.dms.storage.model.SummaryQuota;
import com.flowiee.dms.storage.dto.DocumentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface DocumentInfoService {
    Optional<DocumentDTO> findById(Long id);

    Page<DocumentDTO> findDocuments(Integer pageSize, Integer pageNum, Long parentId, List<Long> listId, String isFolder, String pTxtSearch, Long pDocType, Boolean isDeleted, boolean pIsScheduleMode);

    Page<Document> findAllDeletedDocument(int pageSize, int pageNum);

    List<DocumentDTO> setInfoRights(List<DocumentDTO> documentDTOs);

    List<DocumentDTO> findSubDocByParentId(Long parentId, Boolean isFolder, boolean fullLevel, boolean onlyBaseInfo, boolean isDeleted, boolean pIsScheduleMode);

    List<Document> findByDoctype(Long docType);

    List<DocumentDTO> findHierarchyOfDocument(Long documentId, Long parentId);

    List<DocumentDTO> findSharedDocFromOthers(Long accountId);

    List<DocMetaModel> getMetadata(Long documentId);

    SummaryQuota getSummaryQuota(int pageSize, int pageNum, String sortBy, Sort.Direction sortMode);

    Page<DocumentDTO> getDocumentsSharedByOthers(int pageSize, int pageNum);
}
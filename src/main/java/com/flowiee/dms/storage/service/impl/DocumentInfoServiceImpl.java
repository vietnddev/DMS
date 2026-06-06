package com.flowiee.dms.storage.service.impl;

import com.flowiee.dms.storage.entity.DocShare;
import com.flowiee.dms.storage.entity.Document;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.storage.model.DocMetaModel;
import com.flowiee.dms.storage.model.SummaryQuota;
import com.flowiee.dms.storage.dto.DocumentDTO;
import com.flowiee.dms.storage.repository.DocShareRepository;
import com.flowiee.dms.storage.repository.DocumentRepository;
import com.flowiee.dms.common.service.BaseService;
import com.flowiee.dms.storage.service.DocumentInfoService;
import com.flowiee.dms.common.utils.AppConstants;
import com.flowiee.dms.common.utils.SecurityUtils;
import com.flowiee.dms.common.utils.constants.DocRight;
import com.flowiee.dms.common.utils.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentInfoServiceImpl extends BaseService implements DocumentInfoService {
    private final EntityManager      entityManager;
    private final DocShareRepository docShareRepository;
    private final DocumentRepository documentRepository;

    @Value("${app.isOracleDB}")
    private Boolean isOracleDB;

    @Override
    public Optional<DocumentDTO> findById(Long id) {
        Optional<Document> document = documentRepository.findById(id);
        return document.map(DocumentDTO::fromDocument);
    }

    @Override
    public Page<DocumentDTO> findDocuments(Integer pageSize, Integer pageNum, Long pDocParentId, List<Long> listId, String isFolder, String pTxtSearch, Long pDocType, Boolean isDeleted, boolean pIsScheduleMode) {
        Pageable pageable = Pageable.unpaged();
        if (pageSize >= 0 && pageNum >= 0) {
            pageable = PageRequest.of(pageNum, pageSize, Sort.by("isFolder", "createdAt").descending());
        }


        Long lvCurrentUserId = pIsScheduleMode ? null : SecurityUtils.getCurrentUser().getId();
        boolean lvIsAdmin = pIsScheduleMode || AppConstants.ADMINISTRATOR.equals(SecurityUtils.getCurrentUser().getUsername());

        Page<Document> documents = documentRepository.findAll(pTxtSearch, pDocParentId, lvCurrentUserId, lvIsAdmin, pDocType, isFolder, listId, isDeleted, pageable);
        return new PageImpl<>(DocumentDTO.fromDocuments(documents.getContent()), pageable, documents.getTotalElements());
    }

    @Override
    public Page<Document> findAllDeletedDocument(int pageSize, int pageNum) {
        Pageable pageable = Pageable.unpaged();
        Page<Document> documentPage = documentRepository.findAllDeletedDocument(pageable);

        List<Long> foDocumentId = new ArrayList<>();
        for (Document d : documentPage.getContent()) {
            if (!d.isFile()) {
                foDocumentId.add(d.getId());
            }
        }

        List<Document> documentResult = new ArrayList<>();
        for (Document d : documentPage.getContent()) {
            if (!foDocumentId.contains(d.getParentId())) {
                documentResult.add(d);
            }
        }

        return new PageImpl<>(documentResult, pageable, documentResult.size());
    }

    @Override
    public List<DocumentDTO> setInfoRights(List<DocumentDTO> documentDTOs) {
        for (DocumentDTO d : documentDTOs) {
            List<DocShare> sharesOfDoc = docShareRepository.findByDocAndAccount(d.getId(), SecurityUtils.getCurrentUser().getId(), null);
            for (DocShare ds : sharesOfDoc) {
                if (DocRight.UPDATE.getValue().equals(ds.getRole())) d.setThisAccCanUpdate(true);
                if (DocRight.DELETE.getValue().equals(ds.getRole())) d.setThisAccCanDelete(true);
                if (DocRight.MOVE.getValue().equals(ds.getRole())) d.setThisAccCanMove(true);
                if (DocRight.SHARE.getValue().equals(ds.getRole())) d.setThisAccCanShare(true);
            }
            if (AppConstants.ADMINISTRATOR.equals(SecurityUtils.getCurrentUser().getUsername())) {
                d.setThisAccCanUpdate(true);
                d.setThisAccCanDelete(true);
                d.setThisAccCanMove(true);
                d.setThisAccCanShare(true);
            }
        }
        return documentDTOs;
    }

    @Override
    public List<DocumentDTO> findSubDocByParentId(Long parentId, Boolean pIsFolder, boolean fullLevel, boolean onlyBaseInfo, boolean isDeleted, boolean isScheduleMode) {
        List<DocumentDTO> lvAllSubDocs = new ArrayList<>();

        String lvIsFolder = Boolean.TRUE.equals(pIsFolder) ? "Y" : "N";

        List<DocumentDTO> lvSubDocsByParent = this.findDocuments(-1, -1, parentId, null, lvIsFolder, null, null, isDeleted, isScheduleMode)
                .getContent();

        if (!fullLevel) {
            lvAllSubDocs.addAll(lvSubDocsByParent);
        } else {
            //Find deeper subs level
            List<DocumentDTO> subFolderTemps = new ArrayList<>();
            for (DocumentDTO dto : lvSubDocsByParent) {
                if (dto.getIsFolder().equals("Y")) {
                    subFolderTemps.add(dto);
                }
                lvAllSubDocs.add(dto);
            }
            for (DocumentDTO tmpFolder : subFolderTemps) {
                lvAllSubDocs.addAll(this.findSubDocByParentId(tmpFolder.getId(), null, true, onlyBaseInfo, isDeleted, isScheduleMode));
            }
        }

        if (!onlyBaseInfo) {
            for (DocumentDTO docDTO : lvAllSubDocs) {
                if (docDTO.getIsFolder().equals("N")) {
                    docDTO.setHasSubFolder("N");
                } else {
                    boolean existsSubDocument = documentRepository.existsSubDocument(docDTO.getId());
                    docDTO.setHasSubFolder(existsSubDocument ? "Y" : "N");
                }
            }
        }

        return lvAllSubDocs;
    }

    @Override
    public List<Document> findByDoctype(Long docTypeId) {
        return documentRepository.findAll(null, null, null, true, docTypeId, null, null, null, Pageable.unpaged()).getContent();
    }

    @Override
    public List<DocumentDTO> findHierarchyOfDocument(Long documentId, Long parentId) {
        List<DocumentDTO> hierarchy = new ArrayList<>();
        String strOracle = "WITH DocumentHierarchy(ID, NAME, AS_NAME, PARENT_ID, H_LEVEL) AS ( " +
                        "    SELECT ID, NAME, AS_NAME, PARENT_ID, 1 " +
                        "    FROM DOCUMENT " +
                        "    WHERE id = ? " +
                        "    UNION ALL " +
                        "    SELECT d.ID, d.NAME, d.AS_NAME ,d.PARENT_ID, dh.H_LEVEL + 1 " +
                        "    FROM DOCUMENT d " +
                        "    INNER JOIN DocumentHierarchy dh ON dh.PARENT_ID = d.id " +
                        "), " +
                        "DocumentToFindParent(ID, NAME, AS_NAME, PARENT_ID, H_LEVEL) AS ( " +
                        "    SELECT ID, NAME, AS_NAME, PARENT_ID, NULL AS H_LEVEL " +
                        "    FROM DOCUMENT " +
                        "    WHERE ID = ? " +
                        ") " +
                        "SELECT ID, NAME, CONCAT(CONCAT(AS_NAME, '-'), ID) AS AS_NAME, PARENT_ID, H_LEVEL " +
                        "FROM DocumentHierarchy " +
                        "UNION ALL " +
                        "SELECT ID, NAME, CONCAT(CONCAT(AS_NAME, '-'), ID) AS AS_NAME, PARENT_ID, H_LEVEL " +
                        "FROM DocumentToFindParent " +
                        "START WITH ID = ? " +
                        "CONNECT BY PRIOR PARENT_ID = ID " +
                        "ORDER BY H_LEVEL DESC";
        String strMySQL = "WITH RECURSIVE DocumentHierarchy AS ( " +
                        "    SELECT ID, NAME, AS_NAME, PARENT_ID, 1 AS H_LEVEL " +
                        "    FROM document " +
                        "    WHERE ID = ? " +
                        "    UNION ALL " +
                        "    SELECT d.ID, d.NAME, d.AS_NAME, d.PARENT_ID, dh.H_LEVEL + 1 " +
                        "    FROM document d " +
                        "    INNER JOIN DocumentHierarchy dh ON dh.PARENT_ID = d.ID " +
                        ") " +
                        "SELECT ID, NAME, CONCAT(AS_NAME, '-', ID) AS AS_NAME, PARENT_ID, H_LEVEL " +
                        "FROM DocumentHierarchy " +
                        "ORDER BY H_LEVEL DESC";
        log.info("Load hierarchy of document (breadcrumb)");
        Query query = entityManager.createNativeQuery(isOracleDB ?  strOracle : strMySQL);
        query.setParameter(1, documentId);
        if (isOracleDB) {
            query.setParameter(2, documentId);
            query.setParameter(3, parentId);
        }
        @SuppressWarnings("unchecked")
        List<Object[]> list = query.getResultList();
        DocumentDTO rootHierarchy = new DocumentDTO();
        rootHierarchy.setId(null);
        rootHierarchy.setName("Home");
        rootHierarchy.setAsName("");
        hierarchy.add(rootHierarchy);
        for (Object[] doc : list) {
            DocumentDTO docDTO = new DocumentDTO();
            docDTO.setId(Long.parseLong(String.valueOf(doc[0])));
            docDTO.setName(String.valueOf(doc[1]));
            docDTO.setAsName(String.valueOf(doc[2]));
            docDTO.setParentId(Long.parseLong(String.valueOf(doc[3])));
            hierarchy.add(docDTO);
        }
        return hierarchy;
    }

    @Override
    public List<DocumentDTO> findSharedDocFromOthers(Long accountId) {
        return DocumentDTO.fromDocuments(documentRepository.findWasSharedDoc(accountId));
    }

    @Override
    public List<DocMetaModel> getMetadata(Long documentId) {
        List<DocMetaModel> listReturn = new ArrayList<>();
        try {
            List<Object[]> listData = documentRepository.findMetadata(documentId);
            if (!listData.isEmpty()) {
                for (Object[] data : listData) {
                    Long lvDocDataId = ObjectUtils.isNotEmpty(data[2]) ? Long.parseLong(String.valueOf(data[2])) : 0;
                    String lvDocDataValue = ObjectUtils.isNotEmpty(data[3]) ? String.valueOf(data[3]) : null;

                    listReturn.add(DocMetaModel.builder()
                            .fieldId(Long.parseLong(String.valueOf(data[0])))
                            .fieldName(String.valueOf(data[1]))
                            .dataId(lvDocDataId)
                            .dataValue(lvDocDataValue)
                            .fieldType(String.valueOf(data[4]))
                            .fieldRequired(String.valueOf(data[5]).equals("1"))
                            .build());
                }
            }
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR.getDescription(), "metadata of document"), ex);
        }
        return listReturn;
    }

    @Override
    public SummaryQuota getSummaryQuota(int pageSize, int pageNum, String pSortBy, Sort.Direction sortMode) {
        String lvSortBy = pSortBy;
        if ("fileSize".equals(pSortBy))
            lvSortBy = "f." + pSortBy;
        Pageable pageable = PageRequest.of(pageNum, pageSize,
                sortMode.equals(Sort.Direction.ASC) ? Sort.by(lvSortBy).ascending() : Sort.by(lvSortBy).descending());
        Page<Object[]> documentPage = documentRepository.findDocumentSortByMemoryUsed(pageable);

        double totalMemoryUsed = 0;
        List<SummaryQuota.DocumentQuota> docQuotas = new ArrayList<>();

        for (Object[] obj : documentPage.getContent()) {//d.id, d.name, d.asName, d.docType, f.fileSize
            BigDecimal memoryUsed = new BigDecimal(String.valueOf(obj[4]));
            totalMemoryUsed += memoryUsed.doubleValue();

            docQuotas.add(SummaryQuota.DocumentQuota.builder()
                    .id(Integer.parseInt(String.valueOf(obj[0])))
                    .icon(null)
                    .name(String.valueOf(obj[1]))
                    .memoryUsed(getMemoryDisplay(memoryUsed, null))
                    .build());
        }

        return SummaryQuota.builder()
                .totalMemoryUsed(getMemoryDisplay(BigDecimal.valueOf(documentRepository.getTotalMemoryUsed()), "GB"))
                .documentQuotaPage(documentPage)
                .documents(docQuotas)
                .build();
    }

    private String getMemoryDisplay(BigDecimal pInputMemory, String mmrUnit) {
        BigDecimal ONE_KB = new BigDecimal("1024");
        BigDecimal ONE_MB = ONE_KB.multiply(ONE_KB);  // 1024 * 1024
        BigDecimal ONE_GB = ONE_KB.multiply(ONE_MB);  // 1024 * 1024 * 1024

        BigDecimal memoryKB = pInputMemory.divide(ONE_KB).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal memoryMB = pInputMemory.divide(ONE_MB).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal memoryGB = pInputMemory.divide(ONE_GB).setScale(2, BigDecimal.ROUND_HALF_UP);

        String mmrDisplay = memoryKB + " KB";

        if (mmrUnit == null) {
            if (memoryKB.compareTo(ONE_KB) > 0) {
                mmrDisplay = memoryMB.toPlainString() + " MB";
            }
            if (memoryMB.compareTo(ONE_KB) > 0) {
                mmrDisplay = memoryGB.toPlainString() + " GB";
            }
        } else {
            switch (mmrUnit.toUpperCase()) {
                case "KB":
                    mmrDisplay = memoryKB.toPlainString() + " KB";
                    break;
                case "MB":
                    mmrDisplay = memoryMB.toPlainString() + " MB";
                    break;
                case "GB":
                    mmrDisplay = memoryGB.toPlainString() + " GB";
                    break;
                default:
                    throw new AppException(String.format("Memory unit %s does not support!", mmrUnit));
            }
        }

        return mmrDisplay;
    }

    @Override
    public Page<DocumentDTO> getDocumentsSharedByOthers(int pageSize, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("createdAt").descending());
        Page<Document> documentPage = documentRepository.findDocumentsSharedByOthers(SecurityUtils.getCurrentUser().getId(), pageable);
        List<DocumentDTO> documentDTOs = DocumentDTO.fromDocuments(documentPage.getContent());
        List<Long> folderIdList = new ArrayList<>();
        for (DocumentDTO dto : documentDTOs) {
            if (!dto.isFile()) {
                folderIdList.add(dto.getId());
            }
        }
        List<DocumentDTO> responseList = new ArrayList<>();
        for (DocumentDTO dto : documentDTOs) {
            if (!folderIdList.contains(dto.getParentId())) {
                responseList.add(dto);
            }
        }

        return new PageImpl<>(responseList, pageable, responseList.size());
    }
}
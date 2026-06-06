package com.flowiee.dms.storage.controller;

import com.flowiee.dms.common.web.BaseController;
import com.flowiee.dms.storage.entity.DocVersion;
import com.flowiee.dms.storage.entity.Document;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.common.exception.BadRequestException;
import com.flowiee.dms.common.model.ApiResponse;
import com.flowiee.dms.storage.model.DownloadResource;
import com.flowiee.dms.storage.model.SummaryQuota;
import com.flowiee.dms.storage.payload.ArchiveDocumentReq;
import com.flowiee.dms.storage.payload.MoveDocumentReq;
import com.flowiee.dms.storage.dto.DocumentDTO;
import com.flowiee.dms.storage.payload.RestoreDocumentReq;
import com.flowiee.dms.storage.payload.RevertDocumentReq;
import com.flowiee.dms.common.utils.FileUtils;
import com.flowiee.dms.common.utils.RequestUtils;
import com.flowiee.dms.common.utils.constants.ErrorCode;
import com.flowiee.dms.common.utils.constants.MessageCode;
import com.flowiee.dms.storage.service.*;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/stg")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocumentController extends BaseController {
    DocActionService docActionService;
    DocArchiveService docArchiveService;
    DocumentInfoService documentInfoService;
    DocDownloadService docDownloadService;
    DocImportService docImportService;

    @Operation(summary = "Find all documents")
    @GetMapping("/doc/all")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ApiResponse<List<DocumentDTO>> getAllDocuments(@RequestParam("pageSize") Integer pageSize,
                                                          @RequestParam("pageNum") Integer pageNum,
                                                          @RequestParam("parentId") Long pParentId,
                                                          @RequestParam(value = "txtSearch", required = false) String txtSearch,
                                                          @RequestParam(value = "isSearch", required = false) Boolean isSearch,
                                                          @RequestParam(value = "docType", required = false) Long pDocType) {
        Long lvParentId = pParentId;
        if (Boolean.TRUE.equals(isSearch) && lvParentId == 0) {//&& ObjectUtils.isNotEmpty(txtSearch)
            lvParentId = null;
        }
        try {
            Page<DocumentDTO> documents = documentInfoService.findDocuments(pageSize, pageNum - 1, lvParentId, null, null, txtSearch, pDocType,false, false);
            List<DocumentDTO> documentIncludeRights = documentInfoService.setInfoRights(documents.getContent());
            return ApiResponse.ok(documentIncludeRights, pageNum, pageSize, documents.getTotalPages(), documents.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR.getDescription(), "documents"), ex);
        }
    }

    @Operation(summary = "Create new document")
    @PostMapping("/doc/create")
    @PreAuthorize("@vldModuleStorage.insertDoc(true)")
    public ApiResponse<DocumentDTO> insertNewDoc(@RequestParam(value = "fileUpload", required = false) MultipartFile fileUpload,
                                                 @RequestParam(value = "docTypeId", required = false) Long docTypeId,
                                                 @RequestParam(value = "name") String name,
                                                 @RequestParam(value = "description", required = false) String description,
                                                 @RequestParam(value = "isFolder") String isFolder,
                                                 @RequestParam(value = "parentId") Long parentId) {
        if ("N".equals(isFolder)) {
            if (fileUpload.isEmpty()) {
                throw new BadRequestException("File attach does not exists!");
            }
            FileUtils.isAllowUpload(FileUtils.getFileExtension(fileUpload.getOriginalFilename()), true, null);
        }
        DocumentDTO document = new DocumentDTO();
        document.setParentId(parentId);
        document.setName(name);
        document.setDescription(description);
        document.setIsFolder(isFolder);
        document.setDocTypeId(docTypeId);
        document.setFileUpload(fileUpload);
        return ApiResponse.ok(docActionService.saveDoc(document));
    }

    @Operation(summary = "Find all folders")
    @GetMapping("/doc/folders")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ApiResponse<List<DocumentDTO>> getAllFolders(@RequestParam(value = "parentId", required = false) Long parentId) {
        try {
            List<DocumentDTO> documentDTOs = documentInfoService.findSubDocByParentId(parentId, true, false, false, false, false);
            return ApiResponse.ok(documentDTOs, 1, 100, 100, documentDTOs.size());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR.getDescription(), "folders"), ex);
        }
    }

    @Operation(summary = "Update document")
    @PutMapping("/doc/update/{id}")
    @PreAuthorize("@vldModuleStorage.updateDoc(true)")
    public ApiResponse<DocumentDTO> updateDoc(@PathVariable("id") Long docId, @ModelAttribute DocumentDTO documentDTO) {
        return ApiResponse.ok(docActionService.updateDoc(documentDTO, docId));
    }

    @Operation(summary = "Delete document")
    @DeleteMapping("/doc/delete/{id}")
    @PreAuthorize("@vldModuleStorage.deleteDoc(true)")
    public ApiResponse<String> deleteDoc(@PathVariable("id") Long docId, @RequestParam(value = "forceDelete", required = false) Boolean forceDelete) {
        if (ObjectUtils.isNotEmpty(forceDelete) && forceDelete.booleanValue()) {
            return ApiResponse.ok(docActionService.deleteDoc(docId, true, true, DocActionService.DELETE_NORMAL));
        }
        return ApiResponse.ok(docActionService.deleteDoc(docId, true));
    }

    @Operation(summary = "Delete document")
    @DeleteMapping("/doc/multi-delete")
    @PreAuthorize("@vldModuleStorage.deleteDoc(true)")
    public ApiResponse<String> deleteDoc(@RequestParam(value = "ids") List<Long> pListOfSelectedDocuments,
                                         @RequestParam(value = "forceDelete", required = false) Boolean forceDelete) {
        for (long docId : pListOfSelectedDocuments) {
            if (ObjectUtils.isNotEmpty(forceDelete) && forceDelete.booleanValue()) {
                docActionService.deleteDoc(docId, true, true, DocActionService.DELETE_NORMAL);
            } else {
                docActionService.deleteDoc(docId, true);
            }
        }
        return ApiResponse.ok(MessageCode.DELETE_SUCCESS.getDescription());
    }

    @Operation(summary = "Copy document")
    @PostMapping("/doc/copy/{id}")
    @PreAuthorize("@vldModuleStorage.copyDoc(true)")
    public ApiResponse<DocumentDTO> copyDoc(@PathVariable("id") Long docId, @RequestParam("nameCopy") String nameCopy) {
        return ApiResponse.ok(docActionService.copyDoc(docId, null, nameCopy));
    }

    @Operation(summary = "Move document")
    @PutMapping("/doc/move/{id}")
    @PreAuthorize("@vldModuleStorage.moveDoc(true)")
    public ApiResponse<String> moveDoc(@PathVariable("id") Long docId, @RequestBody MoveDocumentReq request) {
        return ApiResponse.ok(docActionService.moveDoc(docId, request.getDestinationId()));
    }

    @Operation(summary = "Move document")
    @PutMapping("/doc/multi-move")
    @PreAuthorize("@vldModuleStorage.moveDoc(true)")
    public ApiResponse<String> moveDoc(@RequestBody MoveDocumentReq request) {
        for (long docId : request.getSelectedDocuments()) {
            docActionService.moveDoc(docId, request.getDestinationId());
        }
        return ApiResponse.ok(MessageCode.UPDATE_SUCCESS.getDescription());
    }

    @Operation(summary = "Download document")
    @GetMapping("/doc/download/{id}")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ResponseEntity<Resource> downloadDoc(@PathVariable("id") Integer documentId) throws IOException {
        DownloadResource download = docDownloadService.download(documentId);
        return ResponseEntity.ok()
                .headers(RequestUtils.createHttpHeader(download.getFileName()))
                .contentLength(download.getContentLength())
                .contentType(MediaType.parseMediaType(download.getContentType()))
                .body(download.getResource());
    }

    @Operation(summary = "Import documents")
    @PostMapping("/doc/import/{parentDocId}")
    @PreAuthorize("@vldModuleStorage.insertDoc(true)")
    public ApiResponse<List<DocumentDTO>> importDoc(@PathVariable("parentDocId") int parentDocId,
                                                    @RequestParam(value = "fileUpload") MultipartFile fileUpload,
                                                    @RequestParam(value = "applyRightsParent", required = false) Boolean pApplyRightsParent) throws IOException {
        String fileExtension = FileUtils.getFileExtension(fileUpload.getOriginalFilename());
        if (!"zip".equals(fileExtension)) {
            throw new BadRequestException("Hệ thống chỉ hỗ trợ file .zip cho chức năng import!");
        }
        boolean applyRightsParent = false;
        if (ObjectUtils.isNotEmpty(pApplyRightsParent) && pApplyRightsParent.booleanValue() && parentDocId > 0) applyRightsParent = true;
        return ApiResponse.ok(docImportService.importDocuments(parentDocId, fileUpload, applyRightsParent));
    }

    @Operation(summary = "Find all documents in trash")
    @GetMapping("/doc/trash")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ApiResponse<List<Document>> getDocumentsInTrash(@RequestParam("pageSize") Integer pageSize, @RequestParam("pageNum") Integer pageNum) {
        try {
            Page<Document> documents = documentInfoService.findAllDeletedDocument(pageSize, pageNum - 1);
            return ApiResponse.ok(documents.getContent(), pageNum, pageSize, documents.getTotalPages(), documents.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR.getDescription(), "documents"), ex);
        }
    }

    @Operation(summary = "Restore document")
    @PutMapping("/doc/trash/restore/{documentId}")
    @PreAuthorize("@vldModuleStorage.updateDoc(true)")
    public ApiResponse<String> restoreDocument(@PathVariable("documentId") Integer documentId) {
        if (documentId == null || documentId <= 0) {
            throw new BadRequestException("Request invalid!");
        }
        docActionService.restoreTrash(documentId);
        return ApiResponse.ok("Khôi phục thành công!", null);
    }

    @Operation(summary = "Restore documents")
    @PutMapping("/doc/trash/multi-restore")
    @PreAuthorize("@vldModuleStorage.updateDoc(true)")
    public ApiResponse<String> restoreDocuments(@RequestBody RestoreDocumentReq requestBody) {
        if (requestBody == null || requestBody.getSelectedDocuments() == null) {
            throw new BadRequestException("Request invalid!");
        }
        for (int documentId : requestBody.getSelectedDocuments()) {
            docActionService.restoreTrash(documentId);
        }
        return ApiResponse.ok("Khôi phục thành công!", null);
    }

    @Operation(summary = "Get summary quota")
    @GetMapping("/doc/quota")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ApiResponse<SummaryQuota> getSummaryQuota(@RequestParam("pageSize") Integer pageSize,
                                                     @RequestParam("pageNum") Integer pageNum,
                                                     @RequestParam(value = "sortBy", defaultValue = "fileSize") String sortBy,
                                                     @RequestParam(value = "sort", defaultValue = "desc") String sortMode) {
        SummaryQuota summaryQuota = documentInfoService.getSummaryQuota(pageSize, pageNum - 1, sortBy, Sort.Direction.fromString(sortMode));
        return ApiResponse.ok(summaryQuota, pageNum, pageSize, summaryQuota.getDocumentQuotaPage().getTotalPages(), summaryQuota.getDocuments().size());
    }

    @Operation(summary = "Get documents shared by others")
    @GetMapping("/doc/shared-by-others")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ApiResponse<List<DocumentDTO>> getDocumentsSharedByOtherUsers(@RequestParam("pageSize") Integer pageSize,
                                                                         @RequestParam("pageNum") Integer pageNum) {
        Page<DocumentDTO> documentDTOPage = documentInfoService.getDocumentsSharedByOthers(pageSize, pageNum - 1);
        return ApiResponse.ok(documentDTOPage.getContent(), pageNum, pageSize, documentDTOPage.getTotalPages(), documentDTOPage.getTotalElements());
    }

    @Operation(summary = "Save the current version of the document")
    @PostMapping("/doc/archive-version")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ApiResponse<String> archiveCurrentVersion(@RequestBody ArchiveDocumentReq request) throws DocumentException, IOException {
        if (request.getDocumentId() <= 0) {
            throw new BadRequestException("Document invalid!");
        }
        docArchiveService.archiveVersion(request.getDocumentId(), DocVersion.builder().versionName(request.getVersionName()).build());
        return ApiResponse.ok("Archived successfully!", null);
    }

    @Operation(summary = "Save the current version of the document")
    @PutMapping("/doc/revert-version")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ApiResponse<String> revertVersion(@RequestBody RevertDocumentReq request) throws DocumentException, IOException {
        if (request.getDocumentId() <= 0 || request.getVersionId() <= 0) {
            throw new BadRequestException("Document invalid!");
        }
        docArchiveService.restoreOldVersion(request.getDocumentId(), request.getVersionId());
        return ApiResponse.ok("Reverted successfully!", null);
    }
}
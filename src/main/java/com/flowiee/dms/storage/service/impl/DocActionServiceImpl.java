package com.flowiee.dms.storage.service.impl;

import com.flowiee.dms.account.model.ACTION;
import com.flowiee.dms.account.model.MODULE;
import com.flowiee.dms.common.StartUp;
import com.flowiee.dms.audit.entity.StorageHistory;
import com.flowiee.dms.audit.service.DocHistoryService;
import com.flowiee.dms.category.entity.Category;
import com.flowiee.dms.account.entity.Account;
import com.flowiee.dms.common.utils.*;
import com.flowiee.dms.common.utils.constants.*;
import com.flowiee.dms.notification.entity.Notification;
import com.flowiee.dms.audit.entity.SystemLog;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.common.exception.BadRequestException;
import com.flowiee.dms.common.exception.ResourceNotFoundException;
import com.flowiee.dms.storage.dto.DocumentDTO;
import com.flowiee.dms.storage.dto.FileDTO;
import com.flowiee.dms.storage.entity.*;
import com.flowiee.dms.storage.model.DocMetaModel;
import com.flowiee.dms.storage.model.DocShareModel;
import com.flowiee.dms.storage.repository.DocShareRepository;
import com.flowiee.dms.storage.repository.DocumentRepository;
import com.flowiee.dms.audit.repository.StorageHistoryRepository;
import com.flowiee.dms.common.service.BaseService;
import com.flowiee.dms.account.service.AccountService;
import com.flowiee.dms.notification.service.NotificationService;
import com.flowiee.dms.audit.service.SystemLogService;
import com.flowiee.dms.storage.service.*;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocActionServiceImpl extends BaseService implements DocActionService {
    private final AccountService           accountService;
    private final DocDataService docDataService;
    private final DocShareService docShareService;
    private final DocHistoryService docHistoryService;
    private final FileStorageService fileStorageService;
    private final DocumentRepository documentRepository;
    private final DocShareRepository docShareRepository;
    private final DocumentInfoService documentInfoService;
    private final NotificationService      notificationService;
    private final StorageHistoryRepository storageHistoryRepository;
    private final SystemLogService         systemLogService;

    @Transactional
    @Override
    public DocumentDTO saveDoc(DocumentDTO documentDTO) {
        Long lvParentId = documentDTO.getParentId() != null ? documentDTO.getParentId() : 0L;
        String lvIsFolder = documentDTO.getIsFolder();
        String lvDocumentName = CoreUtils.trim(documentDTO.getName());
        String lvDocumentAliasName = FileUtils.generateAliasName(lvDocumentName);
        String lvDescription = CoreUtils.trim(documentDTO.getDescription());
        Long lvDocTypeId = documentDTO.getDocTypeId();

        Document document = Document.builder()
                .parentId(lvParentId)
                .isFolder(lvIsFolder)
                .name(lvDocumentName)
                .asName(lvDocumentAliasName)
                .description(lvDescription)
                .docType(lvDocTypeId != null ? new Category(lvDocTypeId) : null)
                .build();

        if (documentRepository.existsDocument(document.getParentId(), document.getName(), document.getIsFolder())) {
            throw new BadRequestException("A document with the same name already exists.");
        }

        try {
            Document documentSaved = documentRepository.save(document);
            if ("N".equals(document.getIsFolder()) && documentDTO.getFileUpload() != null) {
                fileStorageService.saveFileOfDocument(documentDTO.getFileUpload(), documentSaved.getId());
            }
            List<DocShare> roleSharesOfDocument = docShareRepository.findByDocument(documentSaved.getParentId());
            for (DocShare docShare : roleSharesOfDocument) {
                docShareService.save(DocShare.builder()
                        .document(new Document(documentSaved.getId()))
                        .account(new Account(docShare.getAccount().getId()))
                        .role(docShare.getRole())
                        .build());
            }

            String message = "Thêm mới tài liệu";
            String content = documentSaved.getName();
            if (ACTION.STG_DOC_COPY.name().equals(documentDTO.getAction())) {
                message = "Sao chép tài liệu";
                content = String.format("Sao chép [%s] từ [%s]", content, documentDTO.getCopySourceName());
            }
            systemLogService.writeLogCreate(MODULE.STORAGE, ACTION.STG_DOC_CREATE, MasterObject.Document, message, content);
            docHistoryService.save(StorageHistory.builder()
                    .document(documentSaved)
                    .title("Thêm mới " + documentSaved.getName())
                    .fieldName(StorageHistory.EMPTY).oldValue(StorageHistory.EMPTY).newValue(StorageHistory.EMPTY)
                    .build());
            log.info("{}: {} {}", DocumentInfoServiceImpl.class.getName(), message, DocumentDTO.fromDocument(documentSaved));

            return DocumentDTO.fromDocument(documentSaved);
        } catch (RuntimeException | IOException | DocumentException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR.getDescription(), "document: ") + ex.getMessage(), ex);
        }
    }

    @Transactional
    @Override
    public DocumentDTO updateDoc(DocumentDTO data, Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found!", false));

        if (!docShareService.isShared(documentId, DocRight.UPDATE.getValue())) {
            throw new BadRequestException(ErrorCode.FORBIDDEN_ERROR.getDescription());
        }

        Document documentBefore = ObjectUtils.clone(document);

        document.setName(data.getName());
        document.setDescription(data.getDescription());
        Document documentUpdated = documentRepository.save(document);

        ChangeLog changeLog = new ChangeLog(documentBefore, documentUpdated);
        docHistoryService.save(documentUpdated, null, null, changeLog, null);
        systemLogService.writeLogUpdate(MODULE.STORAGE, ACTION.STG_DOC_UPDATE, MasterObject.Document, "Update document " + document.getName(), changeLog);
        log.info("{}: Update document docId={}", DocumentInfoServiceImpl.class.getName(), documentId);

        return DocumentDTO.fromDocument(documentRepository.save(document));
    }

    @Transactional
    @Override
    public String updateMetadata(List<DocMetaModel> metaDTOs, Long documentId) {
        Optional<Document> document = documentRepository.findById(documentId);
        if (document.isEmpty()) {
            throw new ResourceNotFoundException("Document not found!", true);
        }

        for (DocMetaModel metaDTO : metaDTOs) {
            DocData docDataCurrent = docDataService.findByFieldIdAndDocId(metaDTO.getFieldId(), documentId);
            if (docDataCurrent != null) {
                docDataService.update(metaDTO.getDataValue(), docDataCurrent.getId());
            } else {
                docDataService.save(DocData.builder()
                        .docField(new DocField(metaDTO.getFieldId()))
                        .document(new Document(documentId))
                        .value(metaDTO.getDataValue())
                        .build());
            }
            //docHistoryService.saveDocDataHistory(document.get(), docDataCurrent, docDataCurrent.getDocField().getName(), docDataCurrent.getValue(), metaDTO.getDataValue());
        }

        systemLogService.writeLogUpdate(MODULE.STORAGE, ACTION.STG_DOC_UPDATE, MasterObject.Document, "Update metadata of " + document.get().getName(), SystemLog.EMPTY, SystemLog.EMPTY);
        log.info(DocumentInfoServiceImpl.class.getName() + ": Update metadata docId=" + documentId);

        return MessageCode.UPDATE_SUCCESS.getDescription();
    }

    @Transactional
    @Override
    public String deleteDoc(Long documentId, boolean isDeleteSubDoc) {
        return deleteDoc(documentId, isDeleteSubDoc, false, DELETE_NORMAL);
    }

    @Transactional
    @Override
    public String deleteDoc(Long documentId, boolean isDeleteSubDoc, boolean forceDelete, int modeDelete) {
        Optional<Document> document = documentRepository.findById(documentId);
        if (document.isEmpty()) {
            throw new ResourceNotFoundException("Document not found! " + documentId, false);
        }

        boolean isCheduleMode = DELETE_SCHEDULE == modeDelete;

        if (!isCheduleMode) {
            if (!docShareService.isShared(documentId, DocRight.DELETE.getValue())) {
                throw new BadRequestException(ErrorCode.FORBIDDEN_ERROR.getDescription());
            }
        }
        if (forceDelete) {
            deleteDoc(documentId);
        } else {
            document.get().setDeletedBy(SecurityUtils.getCurrentUser().getUsername());
            document.get().setDeletedAt(LocalDateTime.now());
            Document documentMovedToTrash = documentRepository.save(document.get());
            docHistoryService.save(StorageHistory.builder()
                    .document(documentMovedToTrash)
                    .title("Chuyển vào thùng rác")
                    .fieldName(StorageHistory.EMPTY).oldValue(StorageHistory.EMPTY).newValue(StorageHistory.EMPTY)
                    .build());
        }

        //Delete sub-docs
        if ("Y".equals(document.get().getIsFolder()) && isDeleteSubDoc) {
            List<DocumentDTO> listSubDocs = documentInfoService.findSubDocByParentId(documentId, null, true, true, false, isCheduleMode);
            for (DocumentDTO subDoc : listSubDocs) {
                if (forceDelete) {
                    deleteDoc(subDoc.getId());
                } else {
                    Document subDocToDelete = Document.fromDocumentDTO(subDoc);//risk bug
                    subDocToDelete.setDeletedBy(SecurityUtils.getCurrentUser().getUsername());
                    subDocToDelete.setDeletedAt(LocalDateTime.now());
                    Document subDocumentMovedToTrash = documentRepository.save(subDocToDelete);
                    docHistoryService.save(StorageHistory.builder()
                            .document(subDocumentMovedToTrash)
                            .title("Chuyển vào thùng rác")
                            .fieldName(StorageHistory.EMPTY).oldValue(StorageHistory.EMPTY).newValue(StorageHistory.EMPTY)
                            .build());
                }
            }
        }

        String title = "Di chuyển tài liệu vào thùng rác";
        if (forceDelete)
            title = "Xóa tài liệu";

        if (DELETE_SCHEDULE == modeDelete) {
            SystemLog systemLog = SystemLog.builder().build();
            systemLog.setIp("TP");
            systemLog.setAccount(accountService.findByUsername(AppConstants.ADMINISTRATOR));
            systemLog.setCreatedBy(-1l);
            systemLogService.writeLog(MODULE.STORAGE, ACTION.STG_DOC_DELETE, MasterObject.Document, LogType.D, title, "id=" + documentId, SystemLog.EMPTY, systemLog);
        } else {
            systemLogService.writeLogDelete(MODULE.STORAGE, ACTION.STG_DOC_DELETE, MasterObject.Document, title, "id=" + documentId);
        }
        log.info("{}: Delete document docId={}", DocumentInfoServiceImpl.class.getName(), documentId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Transactional
    public void deleteDoc(long documentId) {
        deleteFileOfDocument(documentId);
        storageHistoryRepository.deleteAllByDocument(documentId);
        docShareService.deleteByDocument(documentId);
        docDataService.deleteAllByDocument(documentId);
        documentRepository.deleteById(documentId);
    }

    private void deleteFileOfDocument(long documentId) {
        List<FileStorage> fileStorages = fileStorageService.findFilesOfDocument(documentId);
        for (FileStorage fileStorage : fileStorages) {
            String fileExtension = fileStorage.getExtension();
            String filePathStr = "src/main/resources/static" + File.separator + fileStorage.getDirectoryPath() + File.separator + fileStorage.getStorageName();
            String filePathTempStr = "";
            if (FileExtension.DOC.key().equals(fileExtension)
                    || FileExtension.DOCX.key().equals(fileExtension)
                    || FileExtension.XLS.key().equals(fileExtension)
                    || FileExtension.XLSX.key().equals(fileExtension)) {
                filePathTempStr = filePathStr.replace("." + fileExtension, ".pdf");
            }
            try {
                Files.deleteIfExists(Paths.get(filePathStr));
                if (!filePathTempStr.equals("")) {
                    //delete .pdf
                    Files.deleteIfExists(Paths.get(filePathTempStr));
                    //delete .png
                    filePathTempStr = filePathTempStr.replace(".pdf", ".png");
                    Files.deleteIfExists(Paths.get(filePathTempStr));
                }
            } catch (IOException ex) {
                throw new AppException(ex);
            }
        }
    }

    @Transactional
    @Override
    public DocumentDTO copyDoc(Long docId, Long destinationId, String nameCopy) {
        Optional<DocumentDTO> doc = documentInfoService.findById(docId);
        if (doc.isEmpty()) {
            throw new BadRequestException("Document to copy not found!");
        }
        if (!docShareService.isShared(docId, DocRight.CREATE.getValue())) {
            throw new BadRequestException(ErrorCode.FORBIDDEN_ERROR.getDescription());
        }
        if ("Y".equals(doc.get().getIsFolder())) {
            throw new BadRequestException("System does not support copy a document as folder type!");
        }
        //Copy doc
        DocumentDTO docCopy = doc.get();
        docCopy.setId(null);
        docCopy.setAction(ACTION.STG_DOC_COPY.name());
        docCopy.setCopySourceName(docCopy.getName());
        docCopy.setName(nameCopy);
        docCopy.setAsName(FileUtils.generateAliasName(nameCopy));
        Document docCopied = this.saveDoc(docCopy);
        //Copy metadata
        for (DocData docData : docDataService.findByDocument(docId)) {
            DocData docDataNew = DocData.builder()
                    .docField(docData.getDocField())
                    .document(docCopied)
                    .value(docData.getValue())
                    .build();
            docDataService.save(docDataNew);
        }
        //Copy file attach
        Optional<FileStorage> fileUploaded = fileStorageService.getFileActiveOfDocument(docId);
        if (fileUploaded.isPresent()) {
            String newNameFile = generateUniqueStr() + "." + FileUtils.getFileExtension(fileUploaded.get().getStorageName());
            String directoryPath = StartUp.getResourceUploadPath() + File.separator + fileUploaded.get().getDirectoryPath() + File.separator;
            Path pathSrc = Paths.get(directoryPath + fileUploaded.get().getStorageName());
            Path pathDes = Paths.get(directoryPath + newNameFile);
            String pathDesStr = pathDes.toString();
            try {
                FileStorage fileCloneInfo = FileStorage.builder()
                        .module(MODULE.STORAGE.name())
                        .extension(FileUtils.getFileExtension(newNameFile))
                        .originalName(newNameFile)
                        .storageName(newNameFile)
                        .fileSize(pathSrc.toFile().length())
                        .contentType(Files.probeContentType(pathDes))
                        .directoryPath(FileUtils.getUploadPathDir(MODULE.STORAGE.name()).substring(FileUtils.getUploadPathDir(MODULE.STORAGE.name()).indexOf("uploads")))
                        .account(SecurityUtils.getCurrentUser().toAccountEntity())
                        .isActive(true)
                        .customizeName(newNameFile)
                        .document(docCopied)
                        .build();
                FileStorage fileClonedInfo = fileStorageService.save(fileCloneInfo);

                for (FileDTO file : FileUtils.getDocumentFiles(fileUploaded.get())) {
                    if (file.getFile() != null && file.getFile().exists()) {
                        String pathDestinationStr = pathDesStr.replaceAll(FileUtils.getFileExtension(pathDesStr.substring(pathDesStr.lastIndexOf(File.separator) + 1)),
                                FileUtils.getFileExtension(file.getFile().getName()));
                        Path lvPathSource = file.getFile().toPath();
                        Path lvPathDestination = Path.of(pathDestinationStr);
                        Files.copy(lvPathSource, lvPathDestination, StandardCopyOption.COPY_ATTRIBUTES).toFile();
                    }
                }
            } catch (IOException e) {
                log.error("Copy file attachment failed!", e);
            }
        }

        return DocumentDTO.fromDocument(docCopied);
    }

    @Transactional
    @Override
    public String moveDoc(Long docId, Long destinationId) {
        Optional<Document> docToMove = documentRepository.findById(docId);
        if (docToMove.isEmpty()) {
            throw new ResourceNotFoundException("Document to move not found!", false);
        }
        if (documentInfoService.findById(destinationId).isEmpty()) {
            throw new ResourceNotFoundException("Document move to found!", false);
        }
        if (!docShareService.isShared(docId, DocRight.MOVE.getValue())) {
            throw new BadRequestException(ErrorCode.FORBIDDEN_ERROR.getDescription());
        }
        docToMove.get().setParentId(destinationId);
        Document documentMoved = documentRepository.save(docToMove.get());
        docHistoryService.save(StorageHistory.builder()
                .document(documentMoved)
                .title("Di chuyển đến thư mục [" + documentMoved.getName() + "]")
                .fieldName(StorageHistory.EMPTY).oldValue(StorageHistory.EMPTY).newValue(StorageHistory.EMPTY)
                .build());
        return "Move successfully!";
    }

    @Transactional
    @Override
    public List<DocShare> shareDoc(Long pDocId, List<DocShareModel> accountShares, boolean applyForSubFolder) {
        Optional<DocumentDTO> doc = documentInfoService.findById(pDocId);
        if (doc.isEmpty() || accountShares.isEmpty()) {
            throw new ResourceNotFoundException("Document not found!", false);
        }
        if (!docShareService.isShared(doc.get().getId(), DocRight.SHARE.getValue())) {
            throw new BadRequestException(ErrorCode.FORBIDDEN_ERROR.getDescription());
        }
        docShareService.deleteAllByDocument(doc.get().getId());
        List<DocShare> docShared = new ArrayList<>();
        for (DocShareModel model : accountShares) {//1 model -> 1 account use document
            Optional<Account> accountOpt = accountService.findById(SecurityUtils.getCurrentUser().getId());
            if (accountOpt.isEmpty()) {
                continue;
            }
            //Share rights to this document and all sub-docs of them
            doShare(doc.get().getId(), model.getAccountId(), model.getCanRead(), model.getCanUpdate(), model.getCanDelete(), model.getCanMove(), model.getCanShare());
            //Share rights to all of sub-docs
            if (applyForSubFolder) {
                if (doc.get().getIsFolder().equals("Y")) {
                    List<DocumentDTO> subDocs = documentInfoService.findSubDocByParentId(doc.get().getId(), null, true, true, false, false);
                    for (DocumentDTO dto : subDocs) {
                        doShare(dto.getId(), model.getAccountId(), model.getCanRead(), model.getCanUpdate(),model.getCanDelete(), model.getCanMove(), model.getCanShare());
                    }
                }
            }
            //Notify
            notificationService.save(Notification.builder()
                    .receiver(accountOpt.get())
                    .message(String.format("%s đã chia sẽ cho bạn tài liệu '%s'", accountOpt.get().getFullName(), doc.get().getName()))
                    .build());
            docHistoryService.save(StorageHistory.builder()
                    .document(new Document(pDocId))
                    .title("Phân quyền")
                    .fieldName(StorageHistory.EMPTY).oldValue(StorageHistory.EMPTY).newValue(StorageHistory.EMPTY)
                    .build());
        }
        return docShared;
    }

    private List<DocShare> doShare(long docId, long accountId, boolean canRead, boolean canUpdate, boolean canDelete, boolean canMove, boolean canShare) {
        List<DocShare> docShared = new ArrayList<>();
        if (canRead) {
            docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.READ)));
        }
        if (canUpdate) {
            docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.UPDATE)));
        }
        if (canDelete) {
            docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.DELETE)));
        }
        if (canMove) {
            docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.MOVE)));
        }
        if (canShare) {
            docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.SHARE)));
        }
        return docShared;
    }

    @Transactional
    @Override
    public void restoreTrash(long documentId) {
        Optional<Document> documentOpt = documentRepository.findById(documentId);
        if (documentOpt.isEmpty()) {
            throw new BadRequestException(String.format("Document with id %s not found!", documentId));
        }
        Document document = documentOpt.get();
        if (document.getDeletedAt() == null) {
            throw new BadRequestException(String.format("Document with name %s not in the trash!", document.getName()));
        }
        document.setDeletedAt(null);
        document.setDeletedBy(null);
        Document documentRestored = documentRepository.save(document);
        docHistoryService.save(StorageHistory.builder()
                .document(documentRestored)
                .title("Khôi phục khỏi thùng rác")
                .fieldName(StorageHistory.EMPTY).oldValue(StorageHistory.EMPTY).newValue(StorageHistory.EMPTY)
                .build());

        if (!document.isFile()) {
            List<DocumentDTO> subDocDTOs = documentInfoService.findSubDocByParentId(documentId, null, true, true, true, false);
            for (DocumentDTO d : subDocDTOs) {
                documentRepository.setDeleteInformation(d.getId(), null, null);
                docHistoryService.save(StorageHistory.builder()
                        .document(new Document(d.getId()))
                        .title("Khôi phục khỏi thùng rác")
                        .fieldName(StorageHistory.EMPTY).oldValue(StorageHistory.EMPTY).newValue(StorageHistory.EMPTY)
                        .build());
            }
        }
    }

    private String generateUniqueStr() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
package com.flowiee.dms.storage.service.impl;

import com.flowiee.dms.storage.entity.Document;
import com.flowiee.dms.audit.entity.SystemLog;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.common.exception.BadRequestException;
import com.flowiee.dms.common.exception.ResourceNotFoundException;
import com.flowiee.dms.account.model.ACTION;
import com.flowiee.dms.storage.model.FolderTree;
import com.flowiee.dms.account.model.MODULE;
import com.flowiee.dms.storage.dto.DocumentDTO;
import com.flowiee.dms.storage.repository.DocumentRepository;
import com.flowiee.dms.storage.service.DocActionService;
import com.flowiee.dms.storage.service.DocImportService;
import com.flowiee.dms.audit.service.SystemLogService;
import com.flowiee.dms.common.utils.CoreUtils;
import com.flowiee.dms.common.utils.FileUtils;
import com.flowiee.dms.common.utils.constants.LogType;
import com.flowiee.dms.common.utils.constants.MasterObject;
import com.flowiee.dms.common.utils.constants.SystemPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocImportServiceImpl implements DocImportService {
    private final DocumentRepository documentRepository;
    private final DocActionService docActionService;
    private final SystemLogService systemLogService;

    @Override
    @Transactional
    public List<DocumentDTO> importDocuments(long docParentId, MultipartFile zipFile, boolean inheritPermission) throws IOException {
        validateImportRequest(docParentId, zipFile);

        //Tạo thư mục tạm lưu file uploaded
        File folderTemp = createImportTempFolder();

        List<DocumentDTO> listImported = new ArrayList<>();

        try {
            String originalFilename = Path.of(CoreUtils.trim(zipFile.getOriginalFilename()))
                    .getFileName()
                    .toString();

            //Save zipped-uploaded file into temporary folder
            Path fileZipUploadedPath = folderTemp.toPath().resolve(originalFilename);
            zipFile.transferTo(fileZipUploadedPath);

            //Unzip zipped-uploaded file
            File folderExtracted = FileUtils.unzipDirectory(fileZipUploadedPath.toFile(), null);

            // Build model FolderTree ánh xạ từ cấu trúc thư mục/file thực tế sau khi giải nén file upload
            // Build a FolderTree model that maps the actual folder/file structure extracted from the uploaded zip file.
            FolderTree folderTree = FileUtils.buildFolderTree(folderExtracted, 0, docParentId, null);

            //Save to database
            listImported.addAll(importFolderTree(folderTree, inheritPermission));
        } catch (IOException ex) {
            throw new AppException("Import document failed!", ex);
        } finally {
            FileUtils.deleteDirectory(folderTemp.toPath());
        }

        writeImportLog(listImported);

        return listImported;
    }

    private void validateImportRequest(long docParentId, MultipartFile uploadFile) {
        if (docParentId < 0) {
            throw new BadRequestException("Invalid parent document id.");
        }

        if (uploadFile == null || uploadFile.isEmpty()) {
            throw new BadRequestException("Uploaded file is required.");
        }

        String originalFilename = uploadFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BadRequestException("Uploaded file name is invalid.");
        }

        String extension = FileUtils.getFileExtension(originalFilename);
        if (!"zip".equalsIgnoreCase(extension)) {
            throw new BadRequestException("Only zip file is supported for import.");
        }

        if (docParentId > 0) {
            Document parentDocument = documentRepository.findById(docParentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Document not found!", false));

            if (!"Y".equals(parentDocument.getIsFolder())) {
                throw new BadRequestException("Parent document must be a folder.");
            }

            if (parentDocument.getDeletedAt() != null || parentDocument.getDeletedBy() != null) {
                throw new BadRequestException("Cannot import into a deleted folder.");
            }
        }
    }

    private File createImportTempFolder() throws IOException {
        File folderTemp = Path.of(
                FileUtils.getSystemPath(SystemPath.ImportStorageTemp).toString(),
                UUID.randomUUID().toString()
        ).toFile();

        Files.createDirectories(folderTemp.toPath());

        return folderTemp;
    }

    private List<DocumentDTO> importFolderTree(FolderTree folderTree, boolean applyRightsParent) throws IOException {
        List<DocumentDTO> list = new ArrayList<>();

        DocumentDTO docDTO = new DocumentDTO();
        docDTO.setParentId(folderTree.getParentId());
        docDTO.setIsFolder(folderTree.isDirectory() ? "Y" : "N");
        docDTO.setName(folderTree.getName());
        docDTO.setAsName(FileUtils.generateAliasName(folderTree.getName()));
        docDTO.setFileUpload((!folderTree.isDirectory() && folderTree.getFile() != null)
                ? FileUtils.convertFileToMultipartFile(folderTree.getFile())
                : null
        );

        DocumentDTO docDTOSaved = docActionService.saveDoc(docDTO);

        if (applyRightsParent) {
            // TODO: inherit parent rights
            log.warn("System has not inherited rights to sub-documents.");
        }

        list.add(docDTOSaved);

        if (folderTree.isDirectory()) {
            for (FolderTree f : folderTree.getSubFiles()) {
                f.setParentId(docDTOSaved.getId());

                if (f.isDirectory()) {
                    list.addAll(importFolderTree(f, applyRightsParent));
                } else {
                    DocumentDTO docSubDTO = new DocumentDTO();
                    docSubDTO.setParentId(docDTOSaved.getId());
                    docSubDTO.setIsFolder("N");
                    docSubDTO.setName(f.getName());
                    docSubDTO.setAsName(FileUtils.generateAliasName(f.getName()));
                    docSubDTO.setFileUpload(f.getFile() != null
                            ? FileUtils.convertFileToMultipartFile(f.getFile())
                            : null
                    );

                    list.add(docActionService.saveDoc(docSubDTO));
                }
            }
        }

        return list;
    }

    private void writeImportLog(List<DocumentDTO> listImported) {
        String importedDocumentIds = listImported.stream()
                .map(DocumentDTO::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        systemLogService.writeLog(
                MODULE.STORAGE,
                ACTION.STG_DOC_CREATE,
                MasterObject.Document,
                LogType.IM,
                "Import tài liệu",
                importedDocumentIds,
                SystemLog.EMPTY
        );
    }
}

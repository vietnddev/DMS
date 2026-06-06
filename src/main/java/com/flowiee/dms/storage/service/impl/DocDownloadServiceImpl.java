package com.flowiee.dms.storage.service.impl;

import com.flowiee.dms.storage.entity.Document;
import com.flowiee.dms.storage.entity.FileStorage;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.common.exception.BadRequestException;
import com.flowiee.dms.common.exception.ResourceNotFoundException;
import com.flowiee.dms.storage.model.DownloadResource;
import com.flowiee.dms.storage.dto.DocumentDTO;
import com.flowiee.dms.storage.repository.DocumentRepository;
import com.flowiee.dms.common.utils.FileUtils;
import com.flowiee.dms.common.utils.constants.DocRight;
import com.flowiee.dms.common.utils.constants.ErrorCode;
import com.flowiee.dms.common.utils.constants.SystemPath;
import com.flowiee.dms.storage.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocDownloadServiceImpl implements DocDownloadService {
    private final DocumentInfoService documentInfoService;
    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;
    private final FolderTreeService folderTreeService;
    private final DocShareService docShareService;

    @Override
    public DownloadResource download(long documentId) throws IOException {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found!", false));

        validateReadPermission(documentId);

        try {
            File fileResponse = doc.getIsFolder().equals("N")
                    ? prepareFileDownload(doc.getId())
                    : prepareFolderDownload(doc.getId());

            return buildDownloadResource(fileResponse);
        } catch (Exception e) {
            log.error("Failed to download document!", e);
            throw new AppException("Failed to download document!", e);
        }
    }

    private void validateReadPermission(long documentId) {
        if (!docShareService.isShared(documentId, DocRight.READ.getValue())) {
            throw new BadRequestException(ErrorCode.FORBIDDEN_ERROR.getDescription());
        }
    }

    private File prepareFileDownload(long documentId) {
        String errorMsg = "File attachment of this document does not exist!";
        FileStorage fileStorage = fileStorageService.getFileActiveOfDocument(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(errorMsg, false));

        File fileResponse = FileUtils.getFileUploaded(fileStorage);

        if (!fileResponse.exists()) {
            throw new ResourceNotFoundException(errorMsg, false);
        }

        return fileResponse;
    }

    private File prepareFolderDownload(long documentId) throws IOException {
        File folder = createDownloadTempFolder();

        List<DocumentDTO> listSubFolderFullLevel =
                documentInfoService.findSubDocByParentId(documentId, null, true, true, false, false);

        for (DocumentDTO document : listSubFolderFullLevel) {
            processSubDocument(folder, document);
        }

        FileUtils.zipDirectory(folder.getPath(), folder.getPath() + ".zip");

        return new File(folder.getPath() + ".zip");
    }

    private File createDownloadTempFolder() throws IOException {
        File folder = Paths.get(
                FileUtils.getSystemPath(SystemPath.DownloadStorageTemp).toString(),
                UUID.randomUUID().toString()
        ).toFile();

        Files.createDirectories(folder.toPath());

        return folder;
    }

    private void processSubDocument(File downloadFolder, DocumentDTO document) throws IOException {
        DocumentDTO treeNode = folderTreeService.findByDocId(document.getId());
        String relativePath = treeNode != null ? treeNode.getPath() : "";

        if (document.getIsFolder().equals("Y")) {
            createFolder(downloadFolder, relativePath );
        } else {
            copyFile(downloadFolder, document, relativePath );
        }
    }

    private void createFolder(File downloadFolder, String relativePath) throws IOException {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }

        StringBuilder currentPath = new StringBuilder();

        for (String s : relativePath.split("[/\\\\]")) {
            currentPath.append(s.trim()).append(File.separator);
            Files.createDirectories(Path.of(downloadFolder.getPath() + File.separator + currentPath));
        }
    }

    private void copyFile(File downloadFolderRoot, DocumentDTO fileDocument, String relativePath) throws IOException {
        FileStorage fileEntity = fileStorageService.getFileActiveOfDocument(fileDocument.getId()).orElse(null);
        if (fileEntity == null) {
            return;
        }

        File sourceFile = FileUtils.getFileUploaded(fileEntity);
        if (!sourceFile.exists()) {
            return;
        }

        Path sourcePath = Paths.get(sourceFile.toURI());
        Path targetPath = downloadFolderRoot.toPath();

        for (String part : relativePath.split("[/\\\\]")) {
            targetPath = targetPath.resolve(part.trim());
        }

        targetPath = Path.of(targetPath + "." + fileEntity.getExtension());

        createParentDirectories(targetPath);

        Files.copy(sourcePath, targetPath, StandardCopyOption.COPY_ATTRIBUTES);
    }

    private void createParentDirectories(Path targetPath) throws IOException {
        Path parentDirectory = targetPath.getParent();
        if (parentDirectory != null && !Files.exists(parentDirectory)) {
            Files.createDirectories(parentDirectory);
        }
    }

    private DownloadResource buildDownloadResource(File fileResponse) throws IOException {
        return DownloadResource.builder()
                .fileName(fileResponse.getName())
                .contentType(Files.probeContentType(fileResponse.toPath()))
                .contentLength(fileResponse.length())
                .resource(new FileSystemResource(fileResponse))
                .build();
    }
}
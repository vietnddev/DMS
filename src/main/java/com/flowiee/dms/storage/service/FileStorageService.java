package com.flowiee.dms.storage.service;

import com.flowiee.dms.common.service.BaseCurdService;
import com.flowiee.dms.storage.entity.FileStorage;
import com.flowiee.dms.storage.dto.FileDTO;
import com.itextpdf.text.DocumentException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface FileStorageService extends BaseCurdService<FileStorage> {
    FileStorage saveFileOfDocument(MultipartFile fileUpload, Long documentId) throws IOException, DocumentException;

    String saveFileOfImport(MultipartFile fileImport, FileStorage fileInfo) throws IOException;

    String changFileOfDocument(MultipartFile fileUpload, Long documentId) throws IOException, DocumentException;

    Optional<FileStorage> getFileActiveOfDocument(Long documentId);

    List<FileStorage> findFilesOfDocument(Long documentId);

    FileDTO getFileDisplay(long documentId);

    void saveFileAttach(MultipartFile multipartFile, Path dest) throws IOException;

    long getTotalMemoryUsed(long accountId);
}
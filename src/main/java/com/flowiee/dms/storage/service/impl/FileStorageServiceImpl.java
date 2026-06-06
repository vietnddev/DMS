package com.flowiee.dms.storage.service.impl;

import com.flowiee.dms.common.StartUp;
import com.flowiee.dms.common.utils.FileUtils;
import com.flowiee.dms.common.utils.ImageUtils;
import com.flowiee.dms.common.utils.PdfUtils;
import com.flowiee.dms.common.utils.SecurityUtils;
import com.flowiee.dms.storage.entity.Document;
import com.flowiee.dms.storage.entity.FileStorage;
import com.flowiee.dms.config.entity.SystemConfig;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.common.exception.BadRequestException;
import com.flowiee.dms.common.exception.StorageLimitExceededException;
import com.flowiee.dms.common.utils.constants.FileExtension;
import com.flowiee.dms.account.model.MODULE;
import com.flowiee.dms.storage.dto.DocumentDTO;
import com.flowiee.dms.storage.dto.FileDTO;
import com.flowiee.dms.storage.repository.FileStorageRepository;
import com.flowiee.dms.config.repository.SystemConfigRepository;
import com.flowiee.dms.common.service.BaseService;
import com.flowiee.dms.storage.service.DocumentInfoService;
import com.flowiee.dms.storage.service.FileStorageService;
import com.flowiee.dms.notification.service.SendMailService;
import com.flowiee.dms.common.utils.constants.ConfigCode;
import com.flowiee.dms.common.utils.constants.ErrorCode;
import com.flowiee.dms.common.utils.constants.MessageCode;
import com.itextpdf.text.DocumentException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FileStorageServiceImpl extends BaseService implements FileStorageService {
    DocumentInfoService documentInfoService;
    FileStorageRepository fileRepository;
    SystemConfigRepository configRepository;
    SendMailService sendMailService;

    @Override
    public Optional<FileStorage> findById(Long fileId) {
        return fileRepository.findById(fileId);
    }

    @Transactional
    @Override
    public FileStorage save(FileStorage fileStorage) {
        FileStorage fileStorageSaved = fileRepository.save(fileStorage);

        if (fileStorage.getFileAttach() != null) {
            String vldStorageLimitMsg = "The storage limit of the Flowiee system was exceeded!";
            if (!vldStorageLimitValid(fileStorage.getFileAttach().getSize(), true)) {
                throw new StorageLimitExceededException(vldStorageLimitMsg);
            }
            if (!vldStorageLimitValid(fileStorage.getFileAttach().getSize(), false)) {
                try {
                    sendMailService.sendMail("Flowiee System notification", "nguyenducviet.vietnd@gmail.com", vldStorageLimitMsg);
                } catch (UnsupportedEncodingException | MessagingException ex) {
                    log.error(vldStorageLimitMsg, ex);
                }
                throw new StorageLimitExceededException(vldStorageLimitMsg);
            }

            vldResourceUploadPath(true);

            Path pathDest = Paths.get(FileUtils.getUploadPathDir(fileStorage.getModule().toUpperCase()) + File.separator + fileStorageSaved.getStorageName());
            try {
                saveFileAttach(fileStorage.getFileAttach(), pathDest);
            } catch (IOException ex) {
                throw new AppException("An error occurred while saving the attachment!", ex);
            }
        }

        return fileStorageSaved;
    }

    @Override
    public FileStorage update(FileStorage entity, Long entityId) {
        entity.setId(entityId);
        return fileRepository.save(entity);
    }

    @Override
    public Optional<FileStorage> getFileActiveOfDocument(Long documentId) {
        List<FileStorage> listFiles = fileRepository.findFileOfDocument(documentId, true);
        if (listFiles != null && !listFiles.isEmpty()) {
            return Optional.of(listFiles.get(0));
        }
        return Optional.empty();
    }

    @Override
    public List<FileStorage> findFilesOfDocument(Long documentId) {
        return fileRepository.findFileOfDocument(documentId, null);
    }

    @Override
    public FileDTO getFileDisplay(long documentId) {
        Optional<FileStorage> fileStorageOpt = this.getFileActiveOfDocument(documentId);//change in the future
        if (fileStorageOpt.isPresent())
        {
            FileStorage fileStorage = fileStorageOpt.get();
            String extension = "." + fileStorage.getExtension();
            if (FileExtension.DOC.key().equals(fileStorage.getExtension()) || FileExtension.DOCX.key().equals(fileStorage.getExtension()) ||
                    FileExtension.XLS.key().equals(fileStorage.getExtension()) || FileExtension.XLSX.key().equals(fileStorage.getExtension()))
            {
                //pdf
                FileStorage pdfModel = ObjectUtils.clone(fileStorage);
                pdfModel.setStorageName(pdfModel.getStorageName().replaceAll(extension, ".pdf"));
                if (FileUtils.getFileUploaded(pdfModel).exists()) {
                    extension = ".pdf";
                }
                //png
                FileStorage imageModel = ObjectUtils.clone(pdfModel);
                imageModel.setStorageName(imageModel.getStorageName().replaceAll(extension, ".png"));
                if (FileUtils.getFileUploaded(imageModel).exists()) {
                    extension = ".png";
                }
            }
            FileDTO fileDTO = FileDTO.fromFileStorage(fileStorageOpt.get());
            fileDTO.setSrc(fileDTO.getSrc().replace("." + fileDTO.getExtension(), extension));

            return fileDTO;
        }
        return FileDTO.builder().build();
    }

    @Override
    public FileStorage saveFileOfDocument(MultipartFile fileUpload, Long documentId) throws IOException, DocumentException {
        FileStorage fileInfo = new FileStorage(fileUpload, MODULE.STORAGE);
        fileInfo.setCustomizeName(fileUpload.getOriginalFilename());
        fileInfo.setDocument(new Document(documentId));
        fileInfo.setActive(true);
        FileStorage fileSaved = this.save(fileInfo);

        Path path = Paths.get(FileUtils.getUploadPathDir(MODULE.STORAGE.name()) + "/" + fileSaved.getStorageName());
        //saveFileAttach(fileUpload, path);

        try {
            PdfUtils.cloneFileToPdf(path.toFile(), fileSaved.getExtension());
        } catch (RuntimeException ex) {
            if (FileExtension.XLSX.key().equals(fileSaved.getExtension())
                    || FileExtension.XLSX.key().equals(fileSaved.getExtension())) {
                ImageUtils.cloneFileToImg(path.toFile(), null);
            }
        }

        return fileSaved;
    }

    @Override
    public String saveFileOfImport(MultipartFile fileImport, FileStorage fileInfo) throws IOException {
        fileRepository.save(fileInfo);
        fileInfo.setStorageName("I_" + fileInfo.getStorageName());
        //saveFileAttach(fileImport, Paths.get(CommonUtils.getPathDirectory(fileInfo.getModule()) + "/" + fileInfo.getStorageName()));
        return "OK";
    }

    @Override
    public String changFileOfDocument(MultipartFile fileUpload, Long documentId) throws IOException, DocumentException {
        Optional<DocumentDTO> document = documentInfoService.findById(documentId);
        if (document.isEmpty()) {
            throw new BadRequestException();
        }
        //Set inactive cho các version cũ
        List<FileStorage> listDocFile = document.get().getListDocFile();
        for (FileStorage docFile : listDocFile) {
            docFile.setActive(false);
            this.update(docFile, docFile.getId());
        }
        //Save file mới vào hệ thống
        FileStorage fileInfo = new FileStorage(fileUpload, MODULE.STORAGE);
        fileInfo.setCustomizeName(fileUpload.getOriginalFilename());
        fileInfo.setDocument(new Document(documentId));
        fileInfo.setActive(true);
        FileStorage fileSaved = this.save(fileInfo);

        Path path = Paths.get(FileUtils.getUploadPathDir(MODULE.STORAGE.name()) + "/" + fileSaved.getStorageName());
        //saveFileAttach(fileUpload, path);

        try {
            PdfUtils.cloneFileToPdf(path.toFile(), fileSaved.getExtension());
        } catch (RuntimeException ex) {
            if (FileExtension.XLSX.key().equals(fileSaved.getExtension())
                    || FileExtension.XLSX.key().equals(fileSaved.getExtension())) {
                ImageUtils.cloneFileToImg(path.toFile(), null);
            }
        }

        return "OK";
    }

    @Override
    public void saveFileAttach(MultipartFile multipartFile, Path dest) throws IOException {
        if (vldResourceUploadPath(true)) {
            multipartFile.transferTo(dest);
        }
    }

    @Override
    public long getTotalMemoryUsed(long accountId) {
        //Unit is bytes
        return fileRepository.getCurrentStorageUsage(accountId);
    }

    @Override
    public String delete(Long fileId) {
        Optional<FileStorage> fileStorage = fileRepository.findById(fileId);
        if (fileStorage.isEmpty()) {
            throw new AppException(ErrorCode.DELETE_ERROR.getDescription());
        }
        fileRepository.deleteById(fileId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    private boolean vldResourceUploadPath(boolean throwException) {
        if (StartUp.getResourceUploadPath() == null) {
            SystemConfig resourceUploadPathConfig = configRepository.findByCode(ConfigCode.resourceUploadPath.name());
            if (resourceUploadPathConfig != null && ObjectUtils.isNotEmpty(resourceUploadPathConfig.getValue())) {
                StartUp.mvResourceUploadPath = resourceUploadPathConfig.getValue();
                return true;
            } else {
                if (throwException) {
                    throw new AppException("The uploaded file saving directory is not configured, please try again later!");
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean vldStorageLimitValid(long fileSize, boolean isCheckingForUser) {
        long limitOfUser = Long.parseLong(StartUp.getSystemConfig(ConfigCode.storageLimitPerUser).getValue());//default is GB unit
        long limitOfSystem = Long.parseLong(StartUp.getSystemConfig(ConfigCode.storageLimitAllUser).getValue());//default is GB unit
        if (isCheckingForUser) {
            long limitOfUserBytes = limitOfUser * 1024 * 1024 * 1024;
            long memoryUsed = fileRepository.getCurrentStorageUsage(SecurityUtils.getCurrentUser().getId());
            if (memoryUsed + fileSize > limitOfUserBytes) {
                return false;
            }
        } else {
            long limitOfSystemBytes = limitOfSystem * 1024 * 1024 * 1024;
            long memoryUsed = fileRepository.getCurrentStorageUsage(null);
            if (memoryUsed + fileSize > limitOfSystemBytes) {
                return false;
            }
        }
        return true;
    }
}
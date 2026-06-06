package com.flowiee.dms.storage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flowiee.dms.storage.entity.FileStorage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;

import javax.persistence.Transient;
import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileDTO implements Serializable{
    Long id;
    Long documentId;
    Integer sort;
    String name;
    String storageName;
    String originalName;
    String extension;
    String contentType;
    String module;
    String note;
    String uploadBy;
    String src;
    Boolean isActive;
    Boolean status;
    long size;
    byte[] content;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime uploadAt;
    @Transient
    File file;

    public static FileDTO fromFileStorage(FileStorage fileStorage) {
        FileDTO dto = FileDTO.builder().build();
        if (ObjectUtils.isNotEmpty(fileStorage)) {
            dto.setId(fileStorage.getId());
            if (ObjectUtils.isNotEmpty(fileStorage.getDocument())) {
                dto.setDocumentId(fileStorage.getDocument().getId());
            }
            dto.setSort(fileStorage.getSort());
            dto.setName(fileStorage.getCustomizeName());
            dto.setStorageName(fileStorage.getStorageName());
            dto.setOriginalName(fileStorage.getOriginalName());
            dto.setExtension(fileStorage.getExtension());
            dto.setContentType(fileStorage.getContentType());
            dto.setModule(fileStorage.getModule());
            dto.setNote(fileStorage.getNote());
            dto.setUploadBy(fileStorage.getAccount().getUsername());
            dto.setSrc(fileStorage.getDirectoryPath() + "/" + fileStorage.getStorageName());
            dto.setIsActive(fileStorage.isActive());
            dto.setStatus(fileStorage.isStatus());
            dto.setSize(fileStorage.getFileSize() / 1024);
            dto.setContent(fileStorage.getContent());
            dto.setUploadAt(fileStorage.getCreatedAt());
        }
        return dto;
    }

    public static List<FileDTO> fromFileStorages(List<FileStorage> fileStorages) {
        List<FileDTO> list = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(fileStorages)) {
            for (FileStorage f : fileStorages) {
                list.add(FileDTO.fromFileStorage(f));
            }
        }
        return list;
    }
}
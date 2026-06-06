package com.flowiee.dms.storage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowiee.dms.category.entity.Category;
import com.flowiee.dms.storage.entity.Document;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentDTO extends Document implements Serializable {
    Long docTypeId;
    String docTypeName;
    FileDTO file;
    @JsonIgnore
    MultipartFile fileUpload;
    String hasSubFolder;
    List<DocumentDTO> subFolders;
    String sharedBy;
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy")
    Date sharedAt;
    Boolean thisAccCanUpdate = false;
    Boolean thisAccCanDelete = false;
    Boolean thisAccCanMove = false;
    Boolean thisAccCanShare = false;
    String path;
    @JsonIgnore
    String action;
    @JsonIgnore
    String copySourceName;

    public static DocumentDTO fromDocument(Document document) {
        Category docType = document.getDocType();

        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setName(document.getName());
        dto.setAsName(document.getAsName());
        dto.setDescription(document.getDescription() != null ? document.getDescription() : "-");
        dto.setParentId(document.getParentId());
        if (docType != null) {
            dto.setDocTypeId(docType.getId());
            dto.setDocTypeName(docType.getName() != null ? docType.getName() : "-");
        } else {
            dto.setDocTypeName("-");
        }
        dto.setIsFolder(document.getIsFolder());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setLastUpdatedAt(document.getLastUpdatedAt());
        dto.setLastUpdatedBy(document.getLastUpdatedBy());
        dto.setListDocFile(document.getListDocFile());
        dto.setListDocData(document.getListDocData());
        dto.setListDocShare(document.getListDocShare());
        dto.setListStorageHistory(document.getListStorageHistory());
        return dto;
    }

    public static List<DocumentDTO> fromDocuments(List<Document> documents) {
        List<DocumentDTO> list = new ArrayList<>();
        if (documents != null) {
            for (Document d : documents) {
                list.add(DocumentDTO.fromDocument(d));
            }
        }
        return list;
    }
}
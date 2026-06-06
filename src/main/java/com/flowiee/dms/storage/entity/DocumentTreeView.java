package com.flowiee.dms.storage.entity;

import com.flowiee.dms.common.entity.BaseEntity;
import com.flowiee.dms.storage.dto.DocumentDTO;
import lombok.Data;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Objects;

@Data
@Entity
@Table(name = "document_tree_view")
public class DocumentTreeView extends BaseEntity {
    @Column(name = "parent_id")
    Long parentId;

    @Column(name = "is_folder")
    String isFolder;

    String name;

    @Column(name = "as_name", nullable = false)
    String asName;

    String description;

    @Column(name = "doc_type_id")
    Long documentTypeId;

    @Column(name = "Has_SubFolders")
    String hasSubFolders;

    @Column(name = "Has_SubFiles")
    String hasSubFiles;

    @Column(name = "SubFoldersId")
    String subFoldersId;

    @Column(name = "HierarchyLevel")
    String hierarchyLevel;

    String rowNumm;

    String path;

    public static DocumentDTO toDocDTO(DocumentTreeView docTreeView) {
        DocumentDTO docDTO = new DocumentDTO();
        docDTO.setId(docTreeView.getId());
        docDTO.setParentId(docTreeView.getParentId());
        docDTO.setName(docTreeView.getName());
        docDTO.setAsName(docTreeView.getAsName());
        docDTO.setIsFolder(docTreeView.getIsFolder());
        docDTO.setHasSubFolder(docTreeView.getHasSubFolders());
        docDTO.setPath(docTreeView.getPath());
        docDTO.setCreatedAt(docTreeView.getCreatedAt());
        docDTO.setCreatedBy(docTreeView.getCreatedBy());
        return docDTO;
    }

    public boolean isFile() {
        Assert.notNull(isFolder, "Flag isFolder is null!");
        return Objects.equals(isFolder, "N");
    }
}
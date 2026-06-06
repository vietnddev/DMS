package com.flowiee.dms.audit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.common.entity.BaseHistoryEntity;
import com.flowiee.dms.storage.entity.FileStorage;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "file_storage_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FileStorageHistory extends BaseHistoryEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(name = "version", nullable = false)
    public Long version;

    @Column(name = "entity_id", nullable = false)
    public Long entityId;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "src", nullable = false)
    private String src;

    @Column(name = "is_main_file", nullable = false)
    private boolean isMainFile;

    @Transient
    private FileStorage info;

    public FileStorageHistory(long version) {
        this(version, null);
    }

    public FileStorageHistory(long version, FileStorage info) {
        //setVersion(version);
        setInfo(info);
    }
}
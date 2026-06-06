package com.flowiee.dms.audit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.common.entity.BaseHistoryEntity;
import com.flowiee.dms.storage.entity.Document;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "document_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentHistory extends BaseHistoryEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(name = "version", nullable = false)
    public Long version;

    @Column(name = "entity_id", nullable = false)
    public Long entityId;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "as_name", nullable = false)
    private String asName;

    @Column(name = "description", length = 500)
    private String description;

    @Transient
    Document info;

    public DocumentHistory(long version) {
        this(version, null);
    }

    public DocumentHistory(long version, Document info) {
        setVersion(version);
        setInfo(info);
    }
}
package com.flowiee.dms.audit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.common.entity.BaseHistoryEntity;
import com.flowiee.dms.storage.entity.DocData;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "doc_data_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DocDataHistory extends BaseHistoryEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(name = "version", nullable = false)
    public Long version;

    @Column(name = "document_id", nullable = false)
    public Long documentId;

    @Column(name = "entity_id", nullable = false)
    public Long entityId;

    @Column(name = "doc_field_id", nullable = false)
    private long docFieldId;

    @Column(name = "value")
    private String value;

    @Transient
    private DocData info;

    public DocDataHistory(long version) {
        this(version, null);
    }

    public DocDataHistory(long version, DocData info) {
        setVersion(version);
        setInfo(info);
    }
}
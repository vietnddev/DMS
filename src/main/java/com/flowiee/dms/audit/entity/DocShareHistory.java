package com.flowiee.dms.audit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.common.entity.BaseHistoryEntity;
import com.flowiee.dms.storage.entity.DocShare;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "doc_share_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DocShareHistory extends BaseHistoryEntity implements Serializable {
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

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @Transient
    private DocShare info;
}
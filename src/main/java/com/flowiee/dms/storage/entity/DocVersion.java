package com.flowiee.dms.storage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.common.entity.BaseEntity;
import com.flowiee.dms.audit.entity.DocumentHistory;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Builder
@Entity
@Table(name = "doc_version")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocVersion extends BaseEntity implements Serializable {
    @Column(name = "version", nullable = false)
    Long version;

    @Column(name = "version_name")
    String versionName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    Document document;

    @Transient
    DocumentHistory documentHistoryInfo;

    @Transient
    FileStorage fileStorage;

    @Transient
    List<DocData> docDataList;

    @Transient
    List<DocShare> docShareList;
}
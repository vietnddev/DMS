package com.flowiee.dms.storage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.common.entity.BaseEntity;
import javax.persistence.*;

import com.flowiee.dms.category.entity.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Builder
@Entity
@Table(name = "doc_field")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocField extends BaseEntity implements Serializable {
    @Column(name = "type", nullable = false, length = 20)
    String type;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "min_length", nullable = false)
    Integer minLength;

    @Column(name = "max_length", nullable = false)
    Integer maxLength;

    @Column(name = "min_number", nullable = false)
    Integer minNumber;

    @Column(name = "max_number", nullable = false)
    Integer maxNumber;

    @Column(name = "required", nullable = false)
    Boolean required;

    @Column(name = "sort")
    Integer sort;

    @Column(name = "status", nullable = false)
    Boolean status;

    @JsonIgnoreProperties("listDocField")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_type_id", nullable = false)
    Category docType;

    @JsonIgnoreProperties("docField")
    @OneToMany(mappedBy = "docField", fetch = FetchType.LAZY)
    List<DocData> listDocData;

    public DocField(Long id) {
    	super.id = id;
    }
}
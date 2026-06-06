package com.flowiee.dms.storage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.common.entity.BaseEntity;
import javax.persistence.*;

import com.flowiee.dms.audit.entity.StorageHistory;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Entity
@Table(name = "doc_data")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocData extends BaseEntity implements Serializable {
	@Column(name = "value", length = 2000)
    String value;

    @JsonIgnoreProperties("listDocData")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_field_id", nullable = false)
    DocField docField;

    @JsonIgnoreProperties("listDocData")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    Document document;

    @OneToMany(mappedBy = "docData", fetch = FetchType.LAZY)
    List<StorageHistory> listStorageHistory;

    public Map<String, String> compareTo(DocData entityToCompare) {
        Map<String, String> map = new HashMap<>();
        if (!this.getValue().equals(entityToCompare.getValue())) {
            map.put("DocData - " + this.getDocField().getName(), this.getValue() + "#" + entityToCompare.getValue());
        }
        return map;
    }

	@Override
	public String toString() {
		return "DocData [id=" + super.id + ", content=" + value + ", docField=" + docField + ", document=" + document + "]";
	}        
}
package com.flowiee.dms.category.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.common.entity.BaseEntity;
import javax.persistence.*;

import com.flowiee.dms.storage.entity.DocField;
import com.flowiee.dms.storage.entity.Document;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "category")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category extends BaseEntity implements Serializable {
	@Column(name = "type", length = 20, nullable = false)
	String type;

	@Column(name = "code", length = 20)
	String code;

	@Column(name = "name", length = 50, nullable = false)
	String name;

	@Column(name = "sort")
	Integer sort;

	@Column(name = "icon")
	String icon;

	@Column(name = "color", length = 20)
	String color;

	@Column(name = "parent_id")
	Integer parentId;

	@Column(name = "note", length = 500)
	String note;

	@Column(name = "endpoint", length = 50)
	String endpoint;

	@Column(name = "is_default", length = 1, nullable = false)
	String isDefault;

	@Column(name = "status", length = 20, nullable = false)
	Boolean status;

	@Transient
	Integer totalSubRecords;

	@JsonIgnore
	@OneToMany(mappedBy = "docType", fetch = FetchType.LAZY)
	List<Document> listDocument;

	@JsonIgnore
	@OneToMany(mappedBy = "docType", fetch = FetchType.LAZY)
	List<DocField> listDocfield;

	@JsonIgnore
	@OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
	List<CategoryHistory> listCategoryHistory;

	public Category(Long id) {
		super.id = id;
	}

	public Category(Long id, String name) {
		super.id = id;
		this.name = name;
	}

	public boolean isDefault() {
		return (isDefault == null || isDefault.isBlank()) ? false : "Y".equals(isDefault.trim());
	}

	@Override
	public String toString() {
		return "Category [id= " + super.id + ", type=" + type + ", code=" + code + ", name=" + name + ", sort=" + sort + ", color=" + color
				+ ", note=" + note + ", endpoint=" + endpoint + ", isDefault=" + isDefault + ", status=" + status + "]";
	}
}
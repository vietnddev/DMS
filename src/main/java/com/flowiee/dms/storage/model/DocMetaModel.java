package com.flowiee.dms.storage.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocMetaModel {
	Long fieldId;
	String fieldName;
	Long dataId;
    String dataValue;
    String fieldType;
    Boolean fieldRequired;
	Long docId;

	public DocMetaModel(Long fieldId, String fieldName, Long dataId, String dataValue, String fieldType, Boolean fieldRequired, Long docId) {
		this.fieldId = fieldId;
		this.fieldName = fieldName;
		this.dataId = dataId;
		this.dataValue = dataValue;
		this.fieldType = fieldType;
		this.fieldRequired = fieldRequired;
		this.docId = docId;
	}

	@Override
	public String toString() {
		return "DocMetaDTO [docDataId=" + dataId + ", docDataValue=" + dataValue + ", docFieldName="
				+ fieldName + ", docFieldTypeInput=" + fieldType + ", docFieldRequired=" + fieldRequired + "]";
	}
}
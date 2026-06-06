package com.flowiee.dms.category.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.common.entity.BaseEntity;
import javax.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Builder
@Entity
@Table(name = "ctg_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryHistory extends BaseEntity implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @Column(name = "title", nullable = false, length = 300)
    String title;

    @Column(name = "field", nullable = false, length = 50)
    String field;

    @Lob
    //Oracle @Column(name = "old_value", nullable = false, length = 9999, columnDefinition = "CLOB")
    @Column(name = "old_value", nullable = false, columnDefinition = "TEXT")
    String oldValue;

    @Lob
    //Oracle @Column(name = "new_value", nullable = false, length = 9999, columnDefinition = "CLOB")
    @Column(name = "new_value", nullable = false, columnDefinition = "TEXT")
    String newValue;

	@Override
	public String toString() {
		return "CategoryHistory [id=" + super.id + ", category=" + category + ", title=" + title + ", fieldName=" + field + ", oldValue=" + oldValue + ", newValue=" + newValue + "]";
	}
}
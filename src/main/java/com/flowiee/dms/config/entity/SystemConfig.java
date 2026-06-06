package com.flowiee.dms.config.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.common.entity.BaseEntity;
import javax.persistence.*;

import com.flowiee.dms.common.utils.constants.ConfigCode;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Entity
@Table(name = "config")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SystemConfig extends BaseEntity implements Serializable {
	@Column(name = "code", nullable = false)
	String code;

	@Column(name = "name", nullable = false)
	String name;

	@Column(name = "value", length = 1000)
	String value;

	@Column(name = "sort")
	Integer sort;

	public SystemConfig(String code, String name, String value) {
		this.code = code;
		this.name = name;
		this.value = value;
	}

	public SystemConfig(ConfigCode code, String name, String value) {
		this(code.name(), name, value);
	}

	@Override
	public String toString() {
		return "SystemConfig [id=" + super.id + ", code=" + code + ", name=" + name + ", value=" + value + ", sort=" + sort + "]";
	}
}
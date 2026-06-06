package com.flowiee.dms.common.entity;

import javax.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Entity
@Table(name = "languages")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Language extends BaseEntity implements Serializable {
	@Column(name = "message_code", nullable = false)
    String code;
    
    @Column(name = "message_key", nullable = false)
    String key;
    
    @Column(name = "message_value", nullable = false)
    String value;
}
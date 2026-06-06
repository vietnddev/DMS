package com.flowiee.dms.storage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.common.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Builder
@Entity
@Table(name = "tags")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tags extends BaseEntity implements Serializable {
    @Column(name = "name", length = 50, unique = true)
    String name;

    @Column(name = "description")
    String description;

    @ManyToMany(mappedBy = "tags")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Document> documents; // Giả sử bạn có một entity `Article` mà bạn muốn gắn thẻ.
}
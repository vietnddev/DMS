package com.flowiee.dms.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowiee.dms.common.utils.SecurityUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditEntity {
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "created_at", updatable = false, columnDefinition = "timestamp default current_timestamp")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "created_by", updatable = false)
    @CreatedBy
    protected Long createdBy;

    @JsonIgnore
    @Column(name = "last_updated_at", columnDefinition = "timestamp default current_timestamp")
    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;

    @JsonIgnore
    @Column(name = "last_updated_by")
    @LastModifiedBy
    private String lastUpdatedBy;

    @JsonIgnore
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @JsonIgnore
    @Column(name = "deleted_by")
    private String deletedBy;

    @PreUpdate
    @PrePersist
    public void updateAudit() {
        if (createdBy == null) {
            createdBy = SecurityUtils.getCurrentUser().getId();
        }
        if (lastUpdatedBy == null) {
            lastUpdatedBy = SecurityUtils.getCurrentUser().getUsername();
        }
    }
}
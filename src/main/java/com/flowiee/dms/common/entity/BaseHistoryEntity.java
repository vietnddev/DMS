package com.flowiee.dms.common.entity;

import javax.persistence.*;

import com.flowiee.dms.common.utils.SecurityUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseHistoryEntity implements Cloneable {
    @Column(name = "created_at", updatable = false, columnDefinition = "timestamp default current_timestamp")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "created_by", updatable = false)
    @CreatedBy
    protected String createdBy;

    @PreUpdate
    @PrePersist
    public void updateAudit() {
        if (createdBy == null) {
            createdBy = SecurityUtils.getCurrentUser().getUsername();
        }
    }
}
package com.flowiee.dms.common.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "mail_status")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class MailStatus implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "ref_id", nullable = false)
    private Long refId;

    @CreatedDate
    @Column(name = "creation_time", nullable = false, updatable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime creationTime;

    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    @Column(name = "error_msg", length = 1000)
    private String errorMsg;

    @Column(name = "status", nullable = false)
    private String status;

    @PreUpdate
    @PrePersist
    public void doAudit() {

    }
}
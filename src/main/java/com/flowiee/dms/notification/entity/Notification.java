package com.flowiee.dms.notification.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.common.entity.BaseEntity;
import com.flowiee.dms.account.entity.Account;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "notification")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification extends BaseEntity implements Serializable {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    Account receiver;

    @Column(name = "message", nullable = false)
    String message;

    @Column(name = "doc_shared_id")
    Integer docSharedId;

    @Column(name = "is_read", nullable = false)
    boolean isRead;
}
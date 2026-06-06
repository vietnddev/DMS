package com.flowiee.dms.notification.service;

import com.flowiee.dms.notification.entity.Notification;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface NotificationService {
    Optional<Notification> findById(Long notifyId);

    Notification save(Notification notify);

    Page<Notification> findByReceive(int pageNum, int pageSize, Long receivedId);
}
package com.flowiee.dms.notification.repository;

import com.flowiee.dms.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("from Notification n where n.receiver.id=:receiver")
    Page<Notification> findByReceiver(@Param("receiver") Long receiver, Pageable pageable);
}
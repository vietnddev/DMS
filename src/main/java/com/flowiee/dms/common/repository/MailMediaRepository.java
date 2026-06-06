package com.flowiee.dms.common.repository;

import com.flowiee.dms.common.entity.MailMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailMediaRepository extends JpaRepository<MailMedia, Long> {
    @Query("from MailMedia md " +
           "where md.id not in (select ms.refId from MailStatus ms where ms.deliveryTime is not null or ms.status = 'error') " +
           "order by md.priority")
    List<MailMedia> getEmailReadyToSend();
}
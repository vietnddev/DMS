package com.flowiee.dms.common.repository;

import com.flowiee.dms.common.entity.MailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailStatusRepository extends JpaRepository<MailStatus, Long> {
}
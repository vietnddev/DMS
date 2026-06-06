package com.flowiee.dms.audit.repository;

import com.flowiee.dms.audit.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    @Query("from SystemLog s where s.createdAt <= :createdTime")
    List<SystemLog> getSystemLogFrom(@Param("createdTime") LocalDateTime createdTime);
}
package com.flowiee.dms.audit.repository;

import com.flowiee.dms.audit.entity.DocShareHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocShareHistoryRepository extends JpaRepository<DocShareHistory, Long> {
    @Query("from DocShareHistory d where d.version = :versionId")
    List<DocShareHistory> findByVersion(@Param("version") Long versionId);
}
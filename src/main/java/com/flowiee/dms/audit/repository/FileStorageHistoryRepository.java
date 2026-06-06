package com.flowiee.dms.audit.repository;

import com.flowiee.dms.audit.entity.FileStorageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileStorageHistoryRepository extends JpaRepository<FileStorageHistory, Long> {
    @Query("from FileStorageHistory f where f.documentId = :documentId and f.version = :version")
    List<FileStorageHistory> findOldVersion(@Param("documentId") Long documentId, @Param("version") Long versionId);
}
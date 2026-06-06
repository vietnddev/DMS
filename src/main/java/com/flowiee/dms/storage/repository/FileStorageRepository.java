package com.flowiee.dms.storage.repository;

import com.flowiee.dms.storage.entity.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface FileStorageRepository extends JpaRepository<FileStorage, Long> {
    @Query("from FileStorage f where f.document.id=:documentId and (:status is null or f.isActive=:status) order by f.isActive, f.createdAt desc")
    List<FileStorage> findFileOfDocument(@Param("documentId") Long documentId, @Param("status") Boolean status);

    @Query("select coalesce(sum(f.fileSize), 0) " +
           "from FileStorage f " +
           "where :userId is null or f.createdBy = :userId")
    Long getCurrentStorageUsage(@Param("userId") Long userId);
}
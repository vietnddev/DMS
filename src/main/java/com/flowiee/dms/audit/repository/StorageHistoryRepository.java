package com.flowiee.dms.audit.repository;

import com.flowiee.dms.audit.entity.StorageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageHistoryRepository extends JpaRepository<StorageHistory, Long> {
    @Query("from StorageHistory d where d.document.id=:documentId")
    List<StorageHistory> findByDocument(@Param("documentId") Long documentId);

    @Query("from StorageHistory d where d.docData.id=:docDataId")
    List<StorageHistory> findByDocData(@Param("docDataId") Long docDataId);

    @Modifying
    @Query("delete from StorageHistory d where d.document.id=:documentId")
    void deleteAllByDocument(@Param("documentId") Long documentId);
}
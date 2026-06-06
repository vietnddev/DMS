package com.flowiee.dms.storage.repository;

import com.flowiee.dms.storage.entity.DocVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocVersionRepository extends JpaRepository<DocVersion, Long> {
    @Query("from DocVersion d where d.document.id = :documentId")
    List<DocVersion> findOldVersions(@Param("documentId") long documentId);

    @Query("from DocVersion d where d.document.id = :documentId and d.version = :version")
    DocVersion findOldVersion(@Param("documentId") long documentId, @Param("version") long versionId);

    @Query("select d.version from DocVersion d where d.document.id = :documentId order by d.version desc")
    List<Long> getVersions(@Param("documentId") long documentId);
}
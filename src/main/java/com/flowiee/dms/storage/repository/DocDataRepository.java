package com.flowiee.dms.storage.repository;

import com.flowiee.dms.storage.entity.DocData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocDataRepository extends JpaRepository<DocData, Long> {
    @Query("from DocData d where d.docField.id=:docFieldId")
    List<DocData> findByDocField(@Param("docFieldId") Long docFieldId);

    @Query("from DocData d where d.document.id=:documentId")
    List<DocData> findByDocumentId(@Param("documentId") Long documentId);

    @Query("from DocData d where d.docField.id=:docFieldId and d.document.id=:documentId")
    DocData findByFieldIdAndDocId(@Param("docFieldId") Long docFieldId, @Param("documentId") Long documentId);

    @Modifying
    @Query("update DocData d set d.value=:value where d.id=:docDataId")
    void updateMetaData(@Param("value") String value, @Param("docDataId") Long docDataId);

    @Modifying
    @Query("delete from DocData d where d.document.id=:documentId")
    void deleteAllByDocument(@Param("documentId") Long documentId);
}
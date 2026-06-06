package com.flowiee.dms.storage.repository;

import com.flowiee.dms.storage.entity.DocField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocFieldRepository extends JpaRepository<DocField, Long> {
    @Query("from DocField d where d.docType.id=:docTypeId order by d.sort")
    List<DocField> findByDoctype(@Param("docTypeId") Long docTypeId);
}
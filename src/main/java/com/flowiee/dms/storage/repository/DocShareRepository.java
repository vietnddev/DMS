package com.flowiee.dms.storage.repository;

import com.flowiee.dms.storage.entity.DocShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocShareRepository extends JpaRepository<DocShare, Long> {
    @Query("from DocShare d " +
           "where 1=1 " +
           "and d.document.id=:documentId " +
           "and d.account.id=:accountId " +
           "and (:role is null or d.role = :role)")
    List<DocShare> findByDocAndAccount(@Param("documentId") Long documentId, @Param("accountId") Long accountId, @Param("role") String role);

    @Query("from DocShare d where d.document.id=:documentId")
    List<DocShare> findByDocument(@Param("documentId") Long documentId);

    @Modifying
    @Query("delete from DocShare d where d.account.id=:accountId")
    void deleteAllByAccount(@Param("accountId") Long accountId);

    @Modifying
    @Query("delete from DocShare d where d.document.id=:documentId")
    void deleteAllByDocument(@Param("documentId") Long documentId);
}
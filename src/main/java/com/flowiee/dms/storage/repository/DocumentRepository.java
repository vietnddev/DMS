package com.flowiee.dms.storage.repository;

import com.flowiee.dms.storage.entity.Document;
import com.flowiee.dms.storage.entity.DocumentTreeView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    @Query("select distinct d " +
           "from Document d " +
           "left join DocShare ds " +
           "    on ds.document.id = d.id " +
           "where 1=1 " +
           "    and (:txtSearch is null or d.name like %:txtSearch%) " +
           "    and (:parentId is null or d.parentId=:parentId) " +
           "    and (:isAdmin is true or d.createdBy=:currentAccountId or (ds.account.id=:currentAccountId and ds.role = 'R')) " +
           "    and (:docTypeId is null or d.docType.id=:docTypeId) " +
           "    and (:isFolder is null or d.isFolder=:isFolder) " +
           "    and (:listId is null or d.id in :listId) " +
           "    and (:isDeleted is null or (d.deletedAt is not null and :isDeleted = true) or (d.deletedAt is null and :isDeleted = false))")
    Page<Document> findAll(@Param("txtSearch") String txtSearch,
                           @Param("parentId") Long parentId,
                           @Param("currentAccountId") Long currentAccountId,
                           @Param("isAdmin") boolean isAdmin,
                           @Param("docTypeId") Long docTypeId,
                           @Param("isFolder") String isFolder,
                           @Param("listId") List<Long> listId,
                           @Param("isDeleted") Boolean isDeleted,
                           Pageable pageable);

    @Query("from Document d where d.deletedAt is not null order by d.deletedAt desc")
    Page<Document> findAllDeletedDocument(Pageable pageable);

    @Query(value = "select f.id as field_Id_0, " +
                   "       f.name as field_name_1, " +
                   "       d.id as data_id_2, " +
                   "       d.value as data_value_3, " +
                   "       f.type as field_type_4, " +
                   "       f.required as field_required_5 " +
                   "from doc_field f " +
                   "left join doc_data d" +
                   "    on d.doc_field_id = f.id" +
                   "    and d.document_id = :documentId " +
                   "left join document dc" +
                   "    on dc.doc_type_id = f.doc_type_id" +
                   "    and dc.id = :documentId " +
                   "where 1=1 " +
                   "    and (f.doc_type_id = dc.doc_type_id) " +
                   "    and dc.deleted_at is null " +
                   "order by f.sort",
           nativeQuery = true)
    List<Object[]> findMetadata(@Param("documentId") long documentId);

    @Query("from Document d " +
           "where 1=1 " +
           "    and d.id in (select ds.document.id from DocShare ds where ds.account.id=:accountId) " +
           "    and d.deletedAt is null " +
           "order by d.isFolder, d.createdAt")
    List<Document> findWasSharedDoc(@Param("accountId") Long accountId);

//    @Query("select " +
//           "    count(case when d.isFolder = 'Y' then 1 end) as total_folder, " +
//           "    count(case when d.isFolder = 'N' then 1 end) as total_file, " +
//           "    concat(to_char(sum(f.fileSize) / 1024 / 1024, 'FM9999999990.99'), ' MB') AS total_size " +
//           "from Document d " +
//           "left join FileStorage f " +
//           "    on f.document.id = d.id " +
//           "where " +
//           "    d.deletedAt is null ") Oracle
    @Query("""
        select
            count(case when d.isFolder = 'Y' then 1 end),
            count(case when d.isFolder = 'N' then 1 end),
            concat(function('format', coalesce(sum(f.fileSize), 0) / 1024 / 1024,2), ' MB')
        from Document d
        left join FileStorage f
            on f.document.id = d.id
        where d.deletedAt is null
    """)
    List<Object[]> summaryStorage();

    @Query("select d from DocumentTreeView d " +
           "where 1=1 " +
           "    and (:documentId is null or d.id = :documentId) " +
           "    and (:parentId is null or d.parentId = :parentId) " +
           "    and (:isOnlyFolder is null or d.isFolder = :isOnlyFolder) " +
           "    and d.deletedAt is null " +
           "order by d.path")
    List<DocumentTreeView> findGeneralFolderTree(@Param("documentId") Long documentId,
                                                 @Param("parentId") Long parentId,
                                                 @Param("isOnlyFolder") String isOnlyFolder);

    @Query("select case when count(d) > 0 then true else false end " +
           "from Document d " +
           "where 1=1 " +
           "    and d.parentId = :docId " +
           "    and d.deletedAt is null")
    boolean existsSubDocument(@Param("docId") Long docId);

    @Modifying
    @Query("update Document d set d.deletedAt = :deletedAt, d.deletedBy = :deletedBy where d.id = :documentId")
    void setDeleteInformation(@Param("documentId") long documentId,
                              @Param("deletedAt") LocalDateTime deletedAt,
                              @Param("deletedBy") String deletedBy);

    @Query("from Document d where d.deletedAt <= :dateDelete")
    List<Document> findExpiredDocumentsInRecycleBin(@Param("dateDelete") LocalDateTime dateDelete);

    @Query("select d.id, d.name, d.asName, d.docType, f.fileSize " +
           "from Document d " +
           "inner join FileStorage f " +
           "    on f.document.id = d.id " +
           "    and f.isActive = true " +
           "where d.isFolder = 'N'")
    Page<Object[]> findDocumentSortByMemoryUsed(Pageable pageable);

    @Query("select coalesce(sum(f.fileSize), 0) " +
            "from Document d " +
            "inner join FileStorage f " +
            "    on f.document.id = d.id " +
            "    and f.isActive = true " +
            "where d.isFolder = 'N'")
    long getTotalMemoryUsed();

    @Query("from Document d where d.id in (select ds.document.id from DocShare ds where ds.account.id = :accountId)")
    Page<Document> findDocumentsSharedByOthers(@Param("accountId") Long accountId, Pageable pageable);

    @Query("""
        select (count(d) > 0)
        from Document d
        where d.parentId = :parentId
            and d.name = :name
            and d.isFolder = :isFolder
            and d.deletedAt is null
            and d.deletedBy is null
    """)
    boolean existsDocument(Long parentId, String name, String isFolder);
}
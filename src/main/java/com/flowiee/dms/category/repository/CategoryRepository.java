package com.flowiee.dms.category.repository;

import com.flowiee.dms.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("from Category c where c.code = 'ROOT' order by c.sort")
    List<Category> findRootCategory();

    @Query("from Category c " +
           "where 1=1 " +
           "and (:type is null or c.type=:type) " +
           "and c.code <> 'ROOT' " +
           "and (:parentId is null or c.parentId=:parentId) " +
           "and (COALESCE(:idNotIn) is null or c.id <> :idNotIn) " +
           "order by c.sort")
    Page<Category> findSubCategory(@Param("type") String type, @Param("parentId") Long parentId, @Param("idNotIn") Long idNotIn, Pageable pageable);

    @Query("select c.type, coalesce((select count(*) from Category where code <> 'ROOT' and type = c.type), 0) as total_records " +
            "from Category c " +
            "where c.code = 'ROOT'")
    List<Object[]> totalRecordsOfEachType();
}
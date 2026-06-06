package com.flowiee.dms.category.repository;

import com.flowiee.dms.category.entity.CategoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryHistoryRepository extends JpaRepository<CategoryHistory, Long> {
    @Modifying
    @Query("delete from CategoryHistory where category.id=:categoryId")
    void deleteAllByCategory(@Param("categoryId") Long categoryId);
}
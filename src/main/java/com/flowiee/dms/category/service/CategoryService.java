package com.flowiee.dms.category.service;

import com.flowiee.dms.common.service.BaseCurdService;
import com.flowiee.dms.category.entity.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService extends BaseCurdService<Category> {
    List<Category> findRootCategory();

    Page<Category> findSubCategory(String categoryType, Long parentId, Long idNotIn, int pageSize, int pageNum);
}
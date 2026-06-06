package com.flowiee.dms.category.controller;

import com.flowiee.dms.category.entity.Category;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.common.exception.BadRequestException;
import com.flowiee.dms.common.model.ApiResponse;
import com.flowiee.dms.category.service.CategoryService;
import com.flowiee.dms.common.utils.constants.CategoryType;
import com.flowiee.dms.common.utils.constants.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("${app.api.prefix}/category")
@Tag(name = "Category API", description = "Quản lý danh mục hệ thống")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryController {
    CategoryService categoryService;

    @Operation(summary = "Find all category")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleCategory.readCategory(true)")
    public ApiResponse<List<Category>> findAll() {
        try {
            return ApiResponse.ok(categoryService.findRootCategory());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR.getDescription(), "category"), ex);
        }
    }

    @Operation(summary = "Find by type")
    @GetMapping("/{type}")
    @PreAuthorize("@vldModuleCategory.readCategory(true)")
    public ApiResponse<List<Category>> findByType(@PathVariable("type") String categoryType,
                                                  @RequestParam(value = "parentId", required = false) Long parentId,
                                                  @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                  @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                                  @RequestParam(value = "idNotIn", required = false) Long idNotIn) {
        try {
            if (!CategoryType.isValid(categoryType)) {
                throw new BadRequestException("Category type inValid!");
            }
            if (Objects.isNull(pageSize) || Objects.isNull(pageNum)) {
                return ApiResponse.ok(categoryService.findSubCategory(CategoryType.getByKey(categoryType).name(), parentId, idNotIn, -1, -1).getContent());
            }
            Page<Category> categories = categoryService.findSubCategory(CategoryType.getByKey(categoryType).name(), parentId, null, pageSize, pageNum - 1);
            return ApiResponse.ok(categories.getContent(), pageNum, pageSize, categories.getTotalPages(), categories.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR.getDescription(), "category"), ex);
        }
    }

    @Operation(summary = "Create category")
    @PostMapping("/create")
    @PreAuthorize("@vldModuleCategory.insertCategory(true)")
    public ApiResponse<Category> createCategory(@RequestBody Category category) {
        try {
            category.setType(CategoryType.getByKey(category.getType()).name());
            return ApiResponse.ok(categoryService.save(category));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR.getDescription(), "category"), ex);
        }
    }

    @Operation(summary = "Update category")
    @PutMapping("/update/{categoryId}")
    @PreAuthorize("@vldModuleCategory.updateCategory(true)")
    public ApiResponse<Category> updateCategory(@RequestBody Category category, @PathVariable("categoryId") Long categoryId) {
        try {
            if (categoryService.findById(categoryId).isEmpty()) {
                throw new BadRequestException();
            }
            category.setType(CategoryType.getByKey(category.getType()).name());
            if (category.getCode() == null) {
                category.setCode("");
            }
            if (category.getColor() == null) {
                category.setColor("");
            }
            if (category.getNote() == null) {
                category.setNote("");
            }
            return ApiResponse.ok(categoryService.update(category, categoryId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR.getDescription(), "category"), ex);
        }
    }

    @Operation(summary = "Delete category")
    @DeleteMapping("/delete/{categoryId}")
    @PreAuthorize("@vldModuleCategory.deleteCategory(true)")
    public ApiResponse<String> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        try {
            if (categoryService.findById(categoryId).isEmpty()) {
                throw new BadRequestException();
            }
            return ApiResponse.ok(categoryService.delete(categoryId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.DELETE_ERROR.getDescription(), "category"), ex);
        }
    }
}
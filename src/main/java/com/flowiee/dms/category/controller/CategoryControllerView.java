package com.flowiee.dms.category.controller;

import com.flowiee.dms.common.web.BaseController;
import com.flowiee.dms.category.entity.Category;
import com.flowiee.dms.common.exception.ResourceNotFoundException;
import com.flowiee.dms.category.service.CategoryService;
import com.flowiee.dms.common.utils.PagesUtils;
import com.flowiee.dms.common.utils.constants.CategoryType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@CrossOrigin
@RestController
@RequestMapping("/system/category")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryControllerView extends BaseController {
    CategoryService categoryService;

    @GetMapping
    @PreAuthorize("@vldModuleCategory.readCategory(true)")
    public ModelAndView viewRootCategory() {
        ModelAndView modelAndView = new ModelAndView(PagesUtils.CTG_CATEGORY);
        modelAndView.addObject("category", new Category());
        modelAndView.addObject("listCategory", categoryService.findRootCategory());
        return baseView(modelAndView);
    }

    @GetMapping("/{type}")
    @PreAuthorize("@vldModuleCategory.readCategory(true)")
    public ModelAndView viewSubCategory(@PathVariable("type") String categoryType) {
        if (!CategoryType.isValid(categoryType)) {
            throw new ResourceNotFoundException("Category not found!", true);
        }
        ModelAndView modelAndView = new ModelAndView(PagesUtils.CTG_CATEGORY_DETAIL);
        modelAndView.addObject("categoryType", categoryType);
        modelAndView.addObject("ctgRootName", CategoryType.valueOf(CategoryType.getByKey((categoryType)).getLabel()));
        modelAndView.addObject("url_template", "");
        modelAndView.addObject("url_import", "");
        modelAndView.addObject("url_export", "");
        return baseView(modelAndView);
    }
}
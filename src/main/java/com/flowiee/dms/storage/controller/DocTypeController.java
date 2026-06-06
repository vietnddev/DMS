package com.flowiee.dms.storage.controller;

import com.flowiee.dms.common.web.BaseController;
import com.flowiee.dms.category.entity.Category;
import com.flowiee.dms.common.exception.ResourceNotFoundException;
import com.flowiee.dms.category.service.CategoryService;
import com.flowiee.dms.storage.service.DocFieldService;
import com.flowiee.dms.common.utils.PagesUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@RestController
@RequestMapping("/stg")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocTypeController extends BaseController {
    CategoryService categoryService;
    DocFieldService docFieldService;

    @GetMapping("/doc/doc-type/{id}")
    @PreAuthorize("@vldModuleStorage.updateDoc(true)")
    public ModelAndView viewDocTypeDetail(@PathVariable("id") Long docTypeId) {
        Optional<Category> docType = categoryService.findById(docTypeId);
        if (docType.isEmpty()) {
            throw new ResourceNotFoundException("Document type not found!", false);
        }
        ModelAndView modelAndView = new ModelAndView(PagesUtils.STG_DOCTYPE_DETAIL);
        modelAndView.addObject("docTypeId", docTypeId);
        modelAndView.addObject("docFields", docFieldService.findByDocTypeId(docTypeId));
        return baseView(modelAndView);
    }
}
package com.flowiee.dms.storage.controller;

import com.flowiee.dms.common.utils.FileUtils;
import com.flowiee.dms.common.utils.PagesUtils;
import com.flowiee.dms.common.web.BaseController;
import com.flowiee.dms.category.entity.Category;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.common.exception.ForbiddenException;
import com.flowiee.dms.common.exception.ResourceNotFoundException;
import com.flowiee.dms.storage.model.DocMetaModel;
import com.flowiee.dms.storage.dto.DocumentDTO;
import com.flowiee.dms.storage.service.DocActionService;
import com.flowiee.dms.storage.service.DocShareService;
import com.flowiee.dms.storage.service.DocumentInfoService;
import com.flowiee.dms.storage.service.FileStorageService;
import com.flowiee.dms.category.service.CategoryService;
import com.flowiee.dms.common.utils.constants.CategoryType;
import com.flowiee.dms.common.utils.constants.ErrorCode;
import com.itextpdf.text.DocumentException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.logstash.logback.encoder.org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stg")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocumentControllerView extends BaseController {
    CategoryService     categoryService;
    DocShareService docShareService;
    DocActionService docActionService;
    FileStorageService fileStorageService;
    DocumentInfoService documentInfoService;

    @GetMapping("/dashboard")
    @PreAuthorize("@vldModuleStorage.dashboard(true)")
    public ModelAndView showDashboardOfSTG() {
        ModelAndView modelAndView = new ModelAndView(PagesUtils.STG_DASHBOARD);
        //Loại tài liệu
        List<Category> listLoaiTaiLieu = categoryService.findSubCategory(CategoryType.DOCUMENT_TYPE.getName(), null, null, -1, -1).getContent();
        List<String> listTenOfDocType = new ArrayList<>();
        List<Integer> listSoLuongOfDocType = new ArrayList<>();
        for (Category docType : listLoaiTaiLieu) {
            listTenOfDocType.add(docType.getName());
            listSoLuongOfDocType.add(docType.getListDocument() != null ? docType.getListDocument().size() : 0);
        }
        modelAndView.addObject("reportOfDocType_listTen", listTenOfDocType);
        modelAndView.addObject("reportOfDocType_listSoLuong", listSoLuongOfDocType);
        return baseView(modelAndView);
    }

    @GetMapping("/doc")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ModelAndView viewRootDocuments() {
        ModelAndView modelAndView = new ModelAndView(PagesUtils.STG_DOCUMENT);
        modelAndView.addObject("parentId", 0);
        modelAndView.addObject("folderTree", documentInfoService.findSubDocByParentId(0l, true, false, false, false, false));
        return baseView(modelAndView);
    }

    @GetMapping("/doc/{aliasPath}")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ModelAndView viewSubDocuments(@PathVariable("aliasPath") String aliasPath) {
        String aliasName = aliasPath.substring(0, aliasPath.lastIndexOf("-"));
        long documentId = Long.parseLong(aliasPath.substring(aliasPath.lastIndexOf("-") + 1));
        Optional<DocumentDTO> documentOptional = documentInfoService.findById(documentId);
        if (documentOptional.isEmpty() || !(aliasName + "-" + documentId).equals(documentOptional.get().getAsName() + "-" + documentOptional.get().getId())) {
            throw new ResourceNotFoundException("Document not found!", true);
        }
        if (!docShareService.isShared(documentId, null)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_ERROR.getDescription());
        }
        DocumentDTO document = documentOptional.get();
        try {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("docBreadcrumb", documentInfoService.findHierarchyOfDocument(document.getId(), document.getParentId()));
            modelAndView.addObject("folderTree", documentInfoService.findSubDocByParentId(0l, true, false, false, false, false));
            modelAndView.addObject("documentParentName", document.getName());
            if (!document.isFile()) {
                modelAndView.setViewName(PagesUtils.STG_DOCUMENT);
                modelAndView.addObject("parentId", document.getId());
            } else {
                DocumentDTO docDTO = DocumentDTO.fromDocument(document);
                docDTO.setFile(fileStorageService.getFileDisplay(documentId));

                modelAndView.setViewName(PagesUtils.STG_DOCUMENT_DETAIL);
                modelAndView.addObject("docDetail", docDTO);
                modelAndView.addObject("docMeta", documentInfoService.getMetadata(document.getId()));
                modelAndView.addObject("documentId", document.getId());
            }
            return baseView(modelAndView);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR.getDescription(), "subDocs/ docDetail"), ex);
        }
    }

    @PostMapping("/doc/change-file/{id}")
    @PreAuthorize("@vldModuleStorage.updateDoc(true)")
    public ModelAndView changeFile(@RequestParam("file") MultipartFile file,
                                   @PathVariable("id") Long documentId,
                                   HttpServletRequest request) throws IOException, DocumentException {
        if (documentId <= 0 || documentInfoService.findById(documentId).isEmpty()) {
            throw new ResourceNotFoundException("Document not found!", true);
        }
        FileUtils.isAllowUpload(FileUtils.getFileExtension(file.getOriginalFilename()), true, null);
        fileStorageService.changFileOfDocument(file, documentId);
        return new ModelAndView("redirect:" + request.getHeader("referer"));
    }

    @PostMapping("/document/update/{id}")
    @PreAuthorize("@vldModuleStorage.updateDoc(true)")
    public ModelAndView update(@ModelAttribute("document") DocumentDTO document, @PathVariable("id") Long documentId, HttpServletRequest request) {
        if (document == null || documentId <= 0 || documentInfoService.findById(documentId).isEmpty()) {
            throw new ResourceNotFoundException("Document not found!", true);
        }
        docActionService.updateDoc(document, documentId);
        return new ModelAndView("redirect:" + request.getHeader("referer"));
    }

    @GetMapping("/doc/update-metadata/{id}")
    @PreAuthorize("@vldModuleStorage.updateDoc(true)")
    public ModelAndView updateMetadata(HttpServletRequest request,
                                       @PathVariable("id") Long documentId,
                                       @RequestParam(value = "fieldId", required = false) Long[] fieldIds,
                                       @RequestParam(value = "dataId", required = false) Long[] dataIds,
                                       @RequestParam(value = "dataValue", required = false) String[] dataValues) {
        if (documentId <= 0 || documentInfoService.findById(documentId).isEmpty()) {
            throw new ResourceNotFoundException("Document not found!", true);
        }
        if (ObjectUtils.isNotEmpty(fieldIds) && ObjectUtils.isNotEmpty(dataIds) && ObjectUtils.isNotEmpty(dataValues)) {
            List<DocMetaModel> metaDTOs = new ArrayList<>();
            for (int i = 0; i <fieldIds.length; i++) {
                long fieldId = fieldIds[i];
                long dataId = dataIds[i];
                String dataValue = dataValues[i];
                metaDTOs.add(new DocMetaModel(fieldId, null, dataId, dataValue, null, null, documentId));
            }
            docActionService.updateMetadata(metaDTOs, documentId);
        }
        return new ModelAndView("redirect:" + request.getHeader("referer"));
    }

    @PostMapping("/document/delete/{id}")
    @PreAuthorize("@vldModuleStorage.deleteDoc(true)")
    public ModelAndView deleteDocument(@PathVariable("id") Long documentId, HttpServletRequest request) {
        if (documentId <= 0 || documentInfoService.findById(documentId).isEmpty()) {
            throw new ResourceNotFoundException("Document not found!", true);
        }
        docActionService.deleteDoc(documentId, true);
        return new ModelAndView("redirect:" + request.getHeader("referer"));
    }

    @PostMapping("/document/move/{id}")
    @PreAuthorize("@vldModuleStorage.moveDoc(true)")
    public ModelAndView moveDocument(@PathVariable("id") Long documentId, HttpServletRequest request) {
        if (documentId <= 0 || documentInfoService.findById(documentId).isEmpty()) {
            throw new ResourceNotFoundException("Document not found!", true);
        }
        return new ModelAndView("redirect:" + request.getHeader("referer"));
    }

    @PostMapping("/document/share/{id}")
    @PreAuthorize("@vldModuleStorage.shareDoc(true)")
    public ModelAndView share(@PathVariable("id") Long documentId, HttpServletRequest request) {
        if (documentId <= 0 || documentInfoService.findById(documentId).isEmpty()) {
            throw new ResourceNotFoundException("Document not found!", true);
        }
        return new ModelAndView("redirect:" + request.getHeader("referer"));
    }

    @GetMapping("/doc/trash")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ModelAndView viewTrash() {
        return baseView(new ModelAndView(PagesUtils.STG_TRASH));
    }

    @GetMapping("/doc/quota")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ModelAndView viewQuota() {
        return baseView(new ModelAndView(PagesUtils.STG_QUOTA));
    }

    @GetMapping("/doc/shared-by-others")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ModelAndView viewDocumentsSharedByOthers() {
        ModelAndView modelAndView = new ModelAndView(PagesUtils.STG_SHARED_BY_OTHERS);
        modelAndView.addObject("parentId", 0);
        modelAndView.addObject("folderTree", documentInfoService.findSubDocByParentId(0l, true, false, false, false, false));
        return baseView(modelAndView);
    }
}
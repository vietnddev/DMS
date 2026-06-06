package com.flowiee.dms.storage.controller;

import com.flowiee.dms.storage.model.EximModel;
import com.flowiee.dms.common.service.ExportService;
import com.flowiee.dms.common.utils.constants.TemplateExport;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api.prefix}/stg/doc")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocExportController {
    @Qualifier("documentExportService")
    ExportService exportService;

    @Operation(summary = "Export data")
    @GetMapping("/export/excel")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ResponseEntity<InputStreamResource> exportToExcel(@RequestParam(value = "parentId", required = false) Integer pParentId,
                                                             @RequestParam(value = "exportAll", required = false) Boolean pExportAll) {
        EximModel model = exportService.exportToExcel(TemplateExport.EX_LIST_OF_DOCUMENTS, null, false);
        return ResponseEntity.ok().headers(model.getHttpHeaders()).body(model.getContent());
    }
}
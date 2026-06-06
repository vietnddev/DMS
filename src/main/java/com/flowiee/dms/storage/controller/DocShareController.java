package com.flowiee.dms.storage.controller;

import com.flowiee.dms.storage.entity.DocShare;
import com.flowiee.dms.common.model.ApiResponse;
import com.flowiee.dms.storage.model.DocShareModel;
import com.flowiee.dms.storage.service.DocActionService;
import com.flowiee.dms.storage.service.DocShareService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/stg")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocShareController {
    DocShareService  docShareService;
    DocActionService docActionService;

    @Operation(summary = "Get detail shared role of document")
    @GetMapping("/doc/share/{id}")
    @PreAuthorize("@vldModuleStorage.shareDoc(true)")
    public ApiResponse<List<DocShareModel>> shareDoc(@PathVariable("id") Long docId) {
        return ApiResponse.ok(docShareService.findDetailRolesOfDocument(docId));
    }

    @Operation(summary = "Share document")
    @PutMapping("/doc/share/{id}")
    @PreAuthorize("@vldModuleStorage.shareDoc(true)")
    public ApiResponse<List<DocShare>> shareDoc(@PathVariable("id") Long docId,
                                                @RequestBody List<DocShareModel> accountShares,
                                                @RequestParam(value = "applyInto", required = false) Boolean applyInto) {
        if (ObjectUtils.isEmpty(applyInto) || !applyInto.booleanValue())
            applyInto = false;
        return ApiResponse.ok(docActionService.shareDoc(docId, accountShares, applyInto));
    }
}
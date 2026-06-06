package com.flowiee.dms.storage.controller;

import com.flowiee.dms.common.model.ApiResponse;
import com.flowiee.dms.storage.dto.FileDTO;
import com.flowiee.dms.storage.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/stg")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FileController {
    FileStorageService fileStorageService;

    @Operation(summary = "Find all files of document")
    @GetMapping("/doc/files/{id}")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ApiResponse<List<FileDTO>> getAllFilesOfDoc(@PathVariable("id") Long docId) {
        return ApiResponse.ok(FileDTO.fromFileStorages(fileStorageService.findFilesOfDocument(docId)));
    }
}
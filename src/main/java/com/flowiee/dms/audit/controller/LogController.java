package com.flowiee.dms.audit.controller;

import com.flowiee.dms.audit.entity.SystemLog;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.common.model.ApiResponse;
import com.flowiee.dms.audit.service.SystemLogService;
import com.flowiee.dms.common.utils.constants.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/sys")
@Tag(name = "Log", description = "Quản lý nhật ký hệ thống")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LogController {
    SystemLogService logService;

    @Operation(summary = "Find all log")
    @GetMapping("/log/all")
    @PreAuthorize("@vldModuleSystem.readLog(true)")
    public ApiResponse<List<SystemLog>> findLogs(@RequestParam("pageSize") int pageSize, @RequestParam("pageNum") int pageNum) {
        try {
            Page<SystemLog> logPage = logService.findAll(pageSize, pageNum - 1);
            return ApiResponse.ok(logPage.getContent(), pageNum, pageSize, logPage.getTotalPages(), logPage.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR.getDescription(), "system log"), ex);
        }
    }
}
package com.flowiee.dms.schedule.controller;

import com.flowiee.dms.common.web.BaseController;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.common.model.ApiResponse;
import com.flowiee.dms.schedule.executor.CleanUpRecycleBinScheduleExecutor;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api.prefix}/sys/schedule")
@Tag(name = "System schedule API", description = "Quản lý lịch trình hệ thống")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ScheduleController extends BaseController {
    CleanUpRecycleBinScheduleExecutor cleanUpRecycleBinScheduleExecutor;

    @PostMapping("/force-run/cleanup-recycle-bin")
    public ApiResponse<String> forceRunCleanUpRecycleBin() {
        try {
            cleanUpRecycleBinScheduleExecutor.execute();
            return ApiResponse.ok("Clean up recycle bin task has been forced to run.");
        } catch (AppException ex) {
            return ApiResponse.fail("Failed to run the clean up recycle bin task: " + ex.getMessage(), ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/force-run/cleanup-folder-download-temp")
    public ApiResponse<String> forceRunCleanUpFolderDownloadTemp() {
        try {
            cleanUpRecycleBinScheduleExecutor.execute();
            return ApiResponse.ok("Clean up folder download temp task has been forced to run.");
        } catch (AppException ex) {
            return ApiResponse.fail("Failed to run the clean up folder download temp task: " + ex.getMessage(), ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
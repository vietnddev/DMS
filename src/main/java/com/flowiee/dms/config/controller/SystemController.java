package com.flowiee.dms.config.controller;

import com.flowiee.dms.common.web.BaseController;
import com.flowiee.dms.common.model.ApiResponse;
import com.flowiee.dms.config.service.ConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api.prefix}/sys")
@Tag(name = "System API", description = "Quản lý hệ thống")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SystemController extends BaseController {
    ConfigService configService;

    @GetMapping("/refresh")
    public ApiResponse<String> refreshApp() {
        return ApiResponse.ok(configService.refreshApp());
    }
}
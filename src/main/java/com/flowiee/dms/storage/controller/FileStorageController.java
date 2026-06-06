package com.flowiee.dms.storage.controller;

import com.flowiee.dms.common.web.BaseController;
import com.flowiee.dms.common.utils.FileUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@Tag(name = "File API", description = "Quản lý tài nguyên hệ thống")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FileStorageController extends BaseController {

    @GetMapping("/uploads/**")//http://host:port/uploads/product/2024/10/3/83cbb1e4-37e9-41f1-8892-1d470ceb0f7c.jpg
    public ResponseEntity<Resource> handleFileRequest(HttpServletRequest request) throws IOException {
        isAuthenticated();
        //product/2024/10/3/83cbb1e4-37e9-41f1-8892-1d470ceb0f7c.jpg
        String pathToFile = extractPathFromPattern(request);
        //D:\Image\ uploads \ product\2024\10\3\83cbb1e4-37e9-41f1-8892-1d470ceb0f7c.jpg
        Path filePath = Paths.get(FileUtils.getFileUploadPath() + File.separator + pathToFile);
        //URL [file:/D:/Image/uploads/product/2024/10/3/83cbb1e4-37e9-41f1-8892-1d470ceb0f7c.jpg]
        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(resource.getFile().toPath());

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private String extractPathFromPattern(HttpServletRequest request) {
        // Lấy URI gốc từ request
        String fullPath = request.getRequestURI();
        // Bỏ đi phần '/uploads/' ở đầu
        return fullPath.substring("/uploads/".length());
    }
}
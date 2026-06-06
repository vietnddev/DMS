package com.flowiee.dms.storage.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
@Builder
public class DownloadResource {
    private String fileName;
    private String contentType;
    private long contentLength;
    private Resource resource;
}
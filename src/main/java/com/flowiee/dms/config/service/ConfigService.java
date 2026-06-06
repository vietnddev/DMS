package com.flowiee.dms.config.service;

import com.flowiee.dms.common.service.BaseCurdService;
import com.flowiee.dms.config.entity.SystemConfig;

import java.util.List;

public interface ConfigService extends BaseCurdService<SystemConfig> {
    List<SystemConfig> findAll();

    String refreshApp();
}
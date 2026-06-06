package com.flowiee.dms.config.service.impl;

import com.flowiee.dms.common.StartUp;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.config.entity.SystemConfig;
import com.flowiee.dms.config.repository.SystemConfigRepository;
import com.flowiee.dms.common.service.BaseService;
import com.flowiee.dms.config.service.ConfigService;
import com.flowiee.dms.common.service.LanguageService;
import com.flowiee.dms.common.utils.constants.ConfigCode;
import com.flowiee.dms.common.utils.constants.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ConfigServiceImpl extends BaseService implements ConfigService {
    LanguageService        languageService;
    SystemConfigRepository sysConfigRepository;

    @Override
    public Optional<SystemConfig> findById(Long id) {
        return sysConfigRepository.findById(id);
    }

    @Override
    public List<SystemConfig> findAll() {
        return sysConfigRepository.findAll();
    }

    @Override
    public SystemConfig save(SystemConfig systemConfig) {
        return sysConfigRepository.save(systemConfig);
    }

    @Override
    public SystemConfig update(SystemConfig systemConfig, Long id) {
        systemConfig.setId(id);
        log.info("Update config success! " + systemConfig.toString());
        return sysConfigRepository.save(systemConfig);
    }

    @Override
    public String delete(Long id) {
        try {
            sysConfigRepository.deleteById(id);
            return MessageCode.DELETE_SUCCESS.getDescription();
        } catch (RuntimeException ex) {
            throw new AppException(ex);
        }
    }

    @Transactional
    @Override
    public String refreshApp() {
        try {
            //
            //List<Category> rootCategories = categoryService.findRootCategory();
            //for (Category c : rootCategories) {
            //    if (c.getType() != null && !c.getType().trim().isEmpty()) {
            //        AppConstants.CATEGORY.valueOf(c.getType()).setLabel(c.getName());
            //    }
            //}
            //
            languageService.reloadMessage("vi");
            languageService.reloadMessage("en");

            SystemConfig resUploadPathConfigMdl = sysConfigRepository.findByCode(ConfigCode.resourceUploadPath.name());
            if (resUploadPathConfigMdl != null) {
                StartUp.mvResourceUploadPath = resUploadPathConfigMdl.getValue();
            }

            List<SystemConfig> systemConfigs = sysConfigRepository.findAll();

            int i = 1;
            return new StringBuilder()
                    .append("Completed the following tasks: ")
                    .append("\n " + i++ + ". ").append("Reload message vi & en")
                    .append("\n " + i++ + ". ").append("Reload resource upload path")
                    .toString();
        } catch (RuntimeException ex) {
            log.error("An error occurred while refresh app", ex);
            throw new AppException();
        }
    }
}
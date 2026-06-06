package com.flowiee.dms.common;

import com.flowiee.dms.config.entity.SystemConfig;
import com.flowiee.dms.config.repository.SystemConfigRepository;
import com.flowiee.dms.common.service.LanguageService;
import com.flowiee.dms.common.utils.FileUtils;
import com.flowiee.dms.common.utils.constants.ConfigCode;
import com.flowiee.dms.common.utils.constants.SystemPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Configuration
public class StartUp {
	private final LanguageService languageService;
	private final SystemConfigRepository configRepository;

    protected Logger logger = LoggerFactory.getLogger(getClass());
    public static Date START_APP_TIME = null;
    public static String mvResourceUploadPath = null;
    public static List<SystemConfig> mvSystemConfigs;

	public StartUp(LanguageService languageService, SystemConfigRepository configRepository) {
		this.languageService = languageService;
		this.configRepository = configRepository;
	}
	
    @Bean
    CommandLineRunner init() {
    	return args -> {
            initData();
            loadLanguageMessages("en");
            loadLanguageMessages("vi");
            initResourceConfig();

            mvSystemConfigs = new ArrayList<>(configRepository.findAll());

            START_APP_TIME = new Date();
        };
    }

    private void initData() {
        String flagConfigCode = ConfigCode.initData.name();
        SystemConfig flagConfigObj = configRepository.findByCode(flagConfigCode);
        if (flagConfigObj == null) {
            List<SystemConfig> cnf = new ArrayList<>();
            cnf.add(initDefaultAudit(ConfigCode.initData, "Initialize initial data for the system", "Y"));
            cnf.add(initDefaultAudit(ConfigCode.emailHost, "Email host", "smtp"));
            cnf.add(initDefaultAudit(ConfigCode.emailPort, "Email port", "587"));
            cnf.add(initDefaultAudit(ConfigCode.emailUser, "Email username", null));
            cnf.add(initDefaultAudit(ConfigCode.emailPass, "Email password", null));
            cnf.add(initDefaultAudit(ConfigCode.sysTimeOut, "Thời gian timeout", "3600"));
            cnf.add(initDefaultAudit(ConfigCode.maxSizeFileUpload, "Dung lượng file tối đa cho phép upload", null));
            cnf.add(initDefaultAudit(ConfigCode.extensionAllowedFileUpload, "Định dạng file được phép upload", null));
            cnf.add(initDefaultAudit(ConfigCode.resourceUploadPath, "Thư mực chứa tệp upload", null));
            cnf.add(initDefaultAudit(ConfigCode.timeStorageFileInRecycleBin, "Thời gian lưu trữ tệp ở thùng rác", "15"));
            cnf.add(initDefaultAudit(ConfigCode.storageLimitPerUser, "Dung lượng lưu trữ của mỗi người dùng", "10"));
            cnf.add(initDefaultAudit(ConfigCode.storageLimitAllUser, "Dung lượng lưu trữ của hệ thống", "1000"));
            cnf.add(initDefaultAudit(ConfigCode.deleteSystemLog, "Xóa nhật ký hệ thống tự động", "N"));
            cnf.add(initDefaultAudit(ConfigCode.dayDeleteSystemLog, "Thời gian xóa nhật ký hệ thống, các nhật ký có thời gian tạo từ >= ? ngày sẽ được xóa tự động", "100"));
            configRepository.saveAll(cnf);
        }
    }
    
    private void loadLanguageMessages(String langCode) {
        languageService.reloadMessage(langCode);
    }

    public void initResourceConfig() {
        SystemConfig systemConfig = configRepository.findByCode(ConfigCode.resourceUploadPath.name());
        if (systemConfig != null) {
            if (systemConfig.getValue() != null) {
                mvResourceUploadPath = systemConfig.getValue().trim();
                logger.info(systemConfig.getCode() + ": " + mvResourceUploadPath);
            }
        }

        Path templateTempForExportPath = FileUtils.getSystemPath(SystemPath.TemplateExportTemp);
        Path folderTempForDownloadPath = FileUtils.getSystemPath(SystemPath.DownloadStorageTemp);
        Path folderTempImportStoragePath = FileUtils.getSystemPath(SystemPath.ImportStorageTemp);
        try {
            if (!Files.exists(templateTempForExportPath)) {
                Files.createDirectories(templateTempForExportPath);
            }
            if (!Files.exists(folderTempForDownloadPath)) {
                Files.createDirectories(folderTempForDownloadPath);
            }
            if (!Files.exists(folderTempImportStoragePath)) {
                Files.createDirectories(folderTempImportStoragePath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SystemConfig initDefaultAudit(ConfigCode code, String name, String value) {
        SystemConfig systemConfig = new SystemConfig(code, name, value);
        systemConfig.setCreatedBy(-1l);
        systemConfig.setLastUpdatedBy("SA");
        return systemConfig;
    }

    public static String getResourceUploadPath() {
        return mvResourceUploadPath;
    }

    public static List<SystemConfig> getSystemConfigs() {
        return mvSystemConfigs;
    }

    public static SystemConfig getSystemConfig(ConfigCode pConfigCode) {
	    for (SystemConfig cnf : getSystemConfigs()) {
	        if (pConfigCode.name().equalsIgnoreCase(cnf.getCode())) {
                return cnf;
            }
        }
	    return null;
    }
}
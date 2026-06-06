package com.flowiee.dms.common.service.impl;

import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.common.exception.BadRequestException;
import com.flowiee.dms.common.entity.Language;
import com.flowiee.dms.common.repository.LanguagesRepository;
import com.flowiee.dms.common.service.BaseService;
import com.flowiee.dms.common.service.LanguageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LanguageServiceImpl extends BaseService implements LanguageService {
	LanguagesRepository languagesRepository;

	@Override
	public Optional<Language> findById(Long langId) {
		return languagesRepository.findById(langId);
	}

	@Override
	public Map<String, String> findAllLanguageMessages(String langCode) {
		List<Language> languageList = languagesRepository.findByCode(langCode);
        Map<String, String> languageMessages = new HashMap<>();
        for (Language language : languageList) {
            languageMessages.put(language.getKey(), language.getValue());
        }
        return languageMessages;
	}

	@Override
	public Language update(Language language, Long langId) {
		if (langId == null || langId <= 0) {
			throw new BadRequestException();
		}
		return languagesRepository.save(language);
	}

	@Override
	public void reloadMessage(String langCode) {
		try {
			Map<String, String> enMessages = this.findAllLanguageMessages(langCode);
			Properties properties = new Properties();

			String outputFolder = System.getProperty("user.dir") + "/languages";
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			//OutputStream outputStream = new FileOutputStream(String.format("src/main/resources/language/messages_%s.properties", langCode));
			OutputStream outputStream = new FileOutputStream(String.format("%s/messages_%s.properties", outputFolder, langCode));
			for (Map.Entry<String, String> entry : enMessages.entrySet()) {
				properties.setProperty(entry.getKey(), entry.getValue());
			}
			properties.store(outputStream, String.format("%s Messages", langCode));
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
}
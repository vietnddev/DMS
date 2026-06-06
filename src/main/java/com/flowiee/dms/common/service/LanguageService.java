package com.flowiee.dms.common.service;

import com.flowiee.dms.common.entity.Language;

import java.util.Map;
import java.util.Optional;

public interface LanguageService {
	Optional<Language> findById(Long langId);
	
	Map<String, String> findAllLanguageMessages(String langCode);
	
	Language update(Language language, Long langId);

	void reloadMessage(String langCode);
}
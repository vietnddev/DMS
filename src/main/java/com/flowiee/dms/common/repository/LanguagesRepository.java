package com.flowiee.dms.common.repository;

import com.flowiee.dms.common.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguagesRepository extends JpaRepository<Language, Long> {
	List<Language> findByCode(String code);
}
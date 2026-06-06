package com.flowiee.dms.config.repository;

import com.flowiee.dms.config.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    @Query("from SystemConfig order by sort")
    List<SystemConfig> findAll();

    SystemConfig findByCode(String code);
}
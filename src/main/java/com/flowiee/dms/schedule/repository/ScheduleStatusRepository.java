package com.flowiee.dms.schedule.repository;

import com.flowiee.dms.schedule.entity.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleStatusRepository extends JpaRepository<ScheduleStatus, Long> {
}
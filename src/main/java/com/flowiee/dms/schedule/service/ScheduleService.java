package com.flowiee.dms.schedule.service;

import com.flowiee.dms.schedule.entity.Schedule;
import com.flowiee.dms.schedule.entity.ScheduleStatus;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.schedule.repository.ScheduleRepository;
import com.flowiee.dms.schedule.repository.ScheduleStatusRepository;
import com.flowiee.dms.common.utils.constants.ScheduleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public abstract class ScheduleService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ScheduleStatusRepository scheduleStatusRepository;

    public abstract void execute();

    public ScheduleStatus startSchedule(ScheduleTask scheduleTask) {
        logger.info("Schedule task " + scheduleTask.name() + " start");
        Optional<Schedule> schedule = scheduleRepository.findById(scheduleTask.name());
        if (schedule.isEmpty()) {
            throw new AppException(String.format("Schedule %s is not defined in the database!", scheduleTask));
        }
        if (!schedule.get().isEnable()) {
            throw new AppException(String.format("Schedule %s is not enable!", scheduleTask));
        }
        return scheduleStatusRepository.save(ScheduleStatus.builder()
                .schedule(schedule.get())
                .startTime(LocalDateTime.now())
                .build());
    }

    public void endSchedule(ScheduleStatus scheduleStatus) {
        if (scheduleStatus == null) {
            return;
        }
        scheduleStatus.setEndTime(LocalDateTime.now());
        scheduleStatus.setDuration(ChronoUnit.SECONDS.between(scheduleStatus.getStartTime(), scheduleStatus.getEndTime()) + " SECOND");
        scheduleStatus.setStatus(scheduleStatus.getErrorMsg() == null ? "success" : "fail");
        scheduleStatusRepository.save(scheduleStatus);
        logger.info("Schedule task " + scheduleStatus.getSchedule().getScheduleId() + " end");
    }
}
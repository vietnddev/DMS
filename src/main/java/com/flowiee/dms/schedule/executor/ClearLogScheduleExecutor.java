package com.flowiee.dms.schedule.executor;

import com.flowiee.dms.audit.entity.EventLog;
import com.flowiee.dms.config.entity.SystemConfig;
import com.flowiee.dms.audit.entity.SystemLog;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.audit.repository.EventLogRepository;
import com.flowiee.dms.schedule.service.ScheduleService;
import com.flowiee.dms.config.repository.SystemConfigRepository;
import com.flowiee.dms.audit.repository.SystemLogRepository;
import com.flowiee.dms.common.utils.constants.ConfigCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ClearLogScheduleExecutor extends ScheduleService {
    private final SystemLogRepository systemLogRepository;
    private final EventLogRepository eventLogRepository;
    private final SystemConfigRepository configRepository;

    @Transactional
    @Scheduled(cron = "0 0 1 * * ?")
    @Override
    public void execute() throws AppException {
        logger.info("ClearLogScheduleExecutor start");

        if (!isEnableDeleteLog()) {
            logger.info("ClearLogScheduleExecutor config " + ConfigCode.deleteSystemLog.name() + " is disable");
            return;
        }

        SystemConfig lvDayDeleteSystemLogConfig = configRepository.findByCode(ConfigCode.dayDeleteSystemLog.name());
        if (lvDayDeleteSystemLogConfig == null || lvDayDeleteSystemLogConfig.getValue() == null || ObjectUtils.isEmpty(lvDayDeleteSystemLogConfig.getValue())) {
            logger.info("ClearLogScheduleExecutor config " + ConfigCode.dayDeleteSystemLog.name() + " is disable");
            return;
        }

        int lvDayDeleteSystemLog = Integer.parseInt(lvDayDeleteSystemLogConfig.getValue());
        LocalDateTime lvFromCreatedTime = LocalDateTime.now().minusDays(lvDayDeleteSystemLog).withNano(0).withSecond(0).withMinute(0).withHour(0);

        List<EventLog> eventLogList = eventLogRepository.getEventLogFrom(lvFromCreatedTime);
        for (EventLog eventLog : eventLogList) {
            eventLogRepository.deleteById(eventLog.getRequestId());
        }
        logger.info("ClearLogScheduleExecutor has completed clearing the event log");

        List<SystemLog> systemLogList = systemLogRepository.getSystemLogFrom(lvFromCreatedTime);
        for (SystemLog systemLog : systemLogList) {
            systemLogRepository.deleteById(systemLog.getId());
        }
        logger.info("ClearLogScheduleExecutor has completed clearing the system log");

        //clear more logs here

        logger.info("ClearLogScheduleExecutor end");
    }

    private boolean isEnableDeleteLog() {
        SystemConfig lvDeleteSystemLogConfig = configRepository.findByCode(ConfigCode.deleteSystemLog.name());
        if (lvDeleteSystemLogConfig == null) {
            return false;
        }
        if (lvDeleteSystemLogConfig.getValue() == null) {
            return false;
        }
        if (!"Y".equals(lvDeleteSystemLogConfig.getValue().trim())) {
            return false;
        }
        return true;
    }
}
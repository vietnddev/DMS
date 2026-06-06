package com.flowiee.dms.schedule.executor;

import com.flowiee.dms.schedule.service.ScheduleService;
import com.flowiee.dms.storage.entity.Document;
import com.flowiee.dms.schedule.entity.ScheduleStatus;
import com.flowiee.dms.config.entity.SystemConfig;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.storage.repository.DocumentRepository;
import com.flowiee.dms.config.repository.SystemConfigRepository;
import com.flowiee.dms.storage.service.DocActionService;
import com.flowiee.dms.common.utils.constants.ConfigCode;
import com.flowiee.dms.common.utils.constants.ScheduleTask;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class CleanUpRecycleBinScheduleExecutor extends ScheduleService {
    DocumentRepository documentRepository;
    DocActionService docActionService;
    SystemConfigRepository systemConfigRepository;

    @Scheduled(cron = "0 */15 * * * ?")
    @Override
    public void execute() {
        logger.info("CleanUpRecycleBinScheduleExecutor start");
        ScheduleStatus scheduleStatus = startSchedule(ScheduleTask.CleanUpRecycleBin);
        try {
            SystemConfig systemConfig = systemConfigRepository.findByCode(ConfigCode.timeStorageFileInRecycleBin.name());
            if (systemConfig != null && systemConfig.getValue() != null) {
                int timeCleanUpRecycleBin = Integer.parseInt(systemConfig.getValue());
                List<Document> expiredDocuments = documentRepository.findExpiredDocumentsInRecycleBin(LocalDateTime.now().minusDays(timeCleanUpRecycleBin));
                for (Document expiredDoc : expiredDocuments) {
                    docActionService.deleteDoc(expiredDoc.getId(), true, true, DocActionService.DELETE_SCHEDULE);
                }
            }
        } catch (AppException ex) {
            log.error("An error occurred while processing schedule {}", ScheduleTask.CleanUpRecycleBin, ex);
            scheduleStatus.setErrorMsg(ex.getMessage());
        } finally {
            endSchedule(scheduleStatus);
        }
        log.info("CleanUpRecycleBinScheduleExecutor end");
    }
}
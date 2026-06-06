package com.flowiee.dms.schedule.executor;

import com.flowiee.dms.schedule.service.ScheduleService;
import com.flowiee.dms.schedule.entity.ScheduleStatus;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.common.utils.FileUtils;
import com.flowiee.dms.common.utils.constants.ScheduleTask;
import com.flowiee.dms.common.utils.constants.SystemPath;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class CleanUpFolderDownloadTempScheduleExecutor extends ScheduleService {

    @Scheduled(cron = "0 0 * * * *")
    @Override
    public void execute() {
        logger.info("CleanUpFolderDownloadTempScheduleExecutor start");
        ScheduleStatus scheduleStatus = startSchedule(ScheduleTask.CleanUpFolderDownloadTemp);
        try {
            File folderTemp = FileUtils.getSystemPath(SystemPath.DownloadStorageTemp).toFile();
            for (File file : Objects.requireNonNull(folderTemp.listFiles())) {
                if (!file.exists()) {
                    System.out.println("File does not exists: " + file.getAbsolutePath());
                    continue;
                }
                if (canDelete(file)) {
                    String message = "Delete successfully";
                    try {
                        FileUtils.deleteDirectory(file.toPath());
                    } catch (IOException e) {
                        message = "Delete fail";
                        logger.error(e.getMessage(), e);
                    } finally {
                        logger.info("Job {} - {} file: '{}'", ScheduleTask.CleanUpFolderDownloadTemp, message, file.getAbsolutePath());
                    }
                }
            }
        } catch (AppException | IOException ex) {
            logger.info(String.format("An error occurred while processing schedule %s", ScheduleTask.CleanUpFolderDownloadTemp), ex);
            scheduleStatus.setErrorMsg(ex.getMessage());
        } finally {
            endSchedule(scheduleStatus);
        }
        logger.info("CleanUpFolderDownloadTempScheduleExecutor end");
    }

    private boolean canDelete(File file) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        FileTime lastAccessTime = attrs.lastAccessTime();
        Date lastAccessDate = new Date(lastAccessTime.toMillis());
        Date now = new Date();
        long diffInMillis = now.getTime() - lastAccessDate.getTime();
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
        //minutes
        int pendingTimeDelete = 10;
        return diffInMinutes >= pendingTimeDelete;
    }
}
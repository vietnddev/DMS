package com.flowiee.dms.schedule.executor;

import com.flowiee.dms.common.entity.MailMedia;
import com.flowiee.dms.common.entity.MailStatus;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.schedule.service.ScheduleService;
import com.flowiee.dms.common.repository.MailMediaRepository;
import com.flowiee.dms.common.repository.MailStatusRepository;
import com.flowiee.dms.notification.service.SendMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MailNotificationScheduleExecutor extends ScheduleService {
    private final SendMailService sendMailService;
    private final MailMediaRepository mailMediaRepository;
    private final MailStatusRepository mailStatusRepository;

    @Scheduled(cron = "0 */15 * * * ?")
    @Override
    public void execute() throws AppException {
        logger.info("MailNotificationScheduleExecutor start");
        int emailSentQty = 0;
        List<MailMedia> emailReadyToSendList = mailMediaRepository.getEmailReadyToSend();
        if (emailReadyToSendList.isEmpty()) {
            emailSentQty = 0;
        } else {
            for (MailMedia mailMedia : emailReadyToSendList) {
                String[] emailDestinationArray = mailMedia.getDestination().split(MailMedia.EMAIL_ADDRESS_SPERATOR);
                String errorMsg = null;
                String sendStatus = "success";
                try {
                    Set<String> lvRecipients = new HashSet<>();
                    for (String emailDestination : emailDestinationArray) {
                        lvRecipients.add(emailDestination.trim());
                    }
                    for (String lvRecipient : lvRecipients) {
                        sendMailService.sendMail(mailMedia.getSubject(), lvRecipient, mailMedia.getMessage(), mailMedia.getAttachment());
                        emailSentQty += 1;
                    }
                } catch (Throwable ex) {
                    logger.error("An error occurred while send an email: " + ex.getMessage(), ex);
                    errorMsg = ex.getMessage();
                    sendStatus = "error";
                } finally {
                    mailStatusRepository.save(MailStatus.builder()
                            .refId(mailMedia.getId())
                            .deliveryTime(errorMsg == null ? LocalDateTime.now() : null)
                            .errorMsg(errorMsg)
                            .status(sendStatus)
                            .build());
                }
            }
        }
        if (emailSentQty == 0) {
            logger.info("No emails have been sent.");
        } else {
            logger.info(emailSentQty + (emailSentQty == 1 ? " email has been sent." : " emails have been sent."));
        }
        logger.info("MailNotificationScheduleExecutor end");
    }
}
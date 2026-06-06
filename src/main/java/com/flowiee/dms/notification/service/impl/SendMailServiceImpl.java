package com.flowiee.dms.notification.service.impl;

import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.common.service.BaseService;
import com.flowiee.dms.notification.service.SendMailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SendMailServiceImpl extends BaseService implements SendMailService {
    JavaMailSender mvJavaMailSender;

    @Override
    public boolean sendMail(String subject, String to, String body) throws UnsupportedEncodingException, MessagingException {
        return sendMail(subject, to, body, null);
    }

    @Override
    public boolean sendMail(String subject, String to, String body, String attachmentPath) throws UnsupportedEncodingException, MessagingException {
        Assert.notNull(subject, "Subject cannot be null!");
        Assert.notNull(to, "Recipient cannot be null!");
        Assert.notNull(body, "Content cannot be null!");

        MimeMessage mimeMessage = mvJavaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        messageHelper.setSubject(subject);
        messageHelper.setTo(to);
        messageHelper.setText(body, true);

        if (ObjectUtils.isNotEmpty(attachmentPath)) {
            File attachmentFile = new File(attachmentPath);

            if (!attachmentFile.exists()) {
                throw new AppException("File attachment not found!");
            }
            long maxSizeInBytes = 10 * 1024 * 1024; // 10MB in bytes
            if (attachmentFile.length() > maxSizeInBytes) {
                throw new AppException("Attachment file size exceeds 10MB limit!");
            }

            messageHelper.addAttachment(attachmentFile.getName(), attachmentFile);
        }

        mvJavaMailSender.send(mimeMessage);

        return true;
    }
}
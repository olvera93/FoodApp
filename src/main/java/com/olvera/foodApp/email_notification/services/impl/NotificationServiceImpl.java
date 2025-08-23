package com.olvera.foodApp.email_notification.services.impl;

import com.olvera.foodApp.email_notification.dtos.NotificationDTO;
import com.olvera.foodApp.email_notification.entity.Notification;
import com.olvera.foodApp.email_notification.repository.NotificationRepository;
import com.olvera.foodApp.email_notification.services.NotificationService;
import com.olvera.foodApp.enums.NotificationType;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl  implements NotificationService {

    private final JavaMailSender javaMailSender;
    private final NotificationRepository notificationRepository;

    @Override
    @Async
    public void sendEmail(NotificationDTO notificationDTO) {
        log.info("Inside sendEmail()");

        try {

            MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                    mimeMailMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            mimeMessageHelper.setTo(notificationDTO.getRecipient());
            mimeMessageHelper.setSubject(notificationDTO.getSubject());
            mimeMessageHelper.setText(notificationDTO.getBody(), notificationDTO.isHtml());

            javaMailSender.send(mimeMailMessage);

            // SAVE TO DATABASE
            Notification notification = Notification.builder()
                    .recipient(notificationDTO.getRecipient())
                    .subject(notificationDTO.getSubject())
                    .body(notificationDTO.getBody())
                    .type(NotificationType.EMAIL)
                    .isHtml(notificationDTO.isHtml())
                    .build();

            notificationRepository.save(notification);

            log.info("Saved to notification table");

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

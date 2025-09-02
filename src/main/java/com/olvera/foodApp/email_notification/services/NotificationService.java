package com.olvera.foodApp.email_notification.services;

import com.olvera.foodApp.email_notification.dtos.NotificationDTO;

public interface NotificationService {

    void sendEmail(NotificationDTO notificationDTO);

}

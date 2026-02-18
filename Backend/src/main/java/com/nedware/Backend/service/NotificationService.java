package com.nedware.Backend.service;

import com.nedware.Backend.domain.Notification;
import com.nedware.Backend.domain.dto.NotificationDto;

import java.util.List;

public interface NotificationService {
    void createNotification(Long recipientId, String message, Notification.NotificationType type);
    List<NotificationDto> getUserNotifications(String userEmail);
    void markAsRead(Long notificationId);
    long getUnreadCount(String userEmail);
}

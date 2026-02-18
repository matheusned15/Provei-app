package com.nedware.Backend.domain.dto;

import com.nedware.Backend.domain.Notification;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private Long id;
    private Long recipientId;
    private String message;
    private Notification.NotificationType type;
    private boolean isRead;
    private LocalDateTime timestamp;
}

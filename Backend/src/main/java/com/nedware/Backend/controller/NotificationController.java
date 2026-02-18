package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.NotificationDto;
import com.nedware.Backend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getUserNotifications(Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(notificationService.getUserNotifications(username));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long id){
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Notification marked as read");
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(notificationService.getUnreadCount(username));
    }
}

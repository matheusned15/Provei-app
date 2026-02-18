package com.nedware.Backend.service.impl;

import com.nedware.Backend.domain.Notification;
import com.nedware.Backend.domain.User;
import com.nedware.Backend.domain.dto.NotificationDto;
import com.nedware.Backend.repository.NotificationRepository;
import com.nedware.Backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = NotificationServiceImpl.class)
class NotificationServiceImplTest {

    @Autowired
    private NotificationServiceImpl service;

    @MockitoBean
    private NotificationRepository notificationRepository;

    @MockitoBean
    private UserRepository userRepository;

    // ---------- helpers ----------

    private User mockUser(Long id, String email, String name) {
        User u = mock(User.class);
        when(u.getId()).thenReturn(id);
        when(u.getEmail()).thenReturn(email);
        when(u.getName()).thenReturn(name);
        return u;
    }

    private Notification mockNotification(
            Long id,
            User recipient,
            String message,
            Notification.NotificationType type,
            boolean read,
            LocalDateTime createdAt
    ) {
        Notification n = mock(Notification.class);
        when(n.getId()).thenReturn(id);
        when(n.getRecipient()).thenReturn(recipient);
        when(n.getMessage()).thenReturn(message);
        when(n.getType()).thenReturn(type);
        when(n.isRead()).thenReturn(read);
        when(n.getCreatedAt()).thenReturn(createdAt);
        return n;
    }

    // ---------- TESTES ----------

    @Test
    @DisplayName("createNotification: cria e salva notificação corretamente")
    void createNotification_ok() {
        User user = mockUser(10L, "x@x.com", "User");
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        service.createNotification(
                10L,
                "Você recebeu uma curtida!",
                Notification.NotificationType.LIKE
        );

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertSame(user, saved.getRecipient());
        assertEquals("Você recebeu uma curtida!", saved.getMessage());
        assertEquals(Notification.NotificationType.LIKE, saved.getType());
    }

    @Test
    @DisplayName("createNotification: lança exceção quando usuário não existe")
    void createNotification_userNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.createNotification(999L, "msg", Notification.NotificationType.FOLLOW)
        );

        assertTrue(ex.getMessage().contains("User not found"));
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("getUserNotifications: retorna lista mapeada corretamente")
    void getUserNotifications_ok() {
        User user = mockUser(10L, "me@x.com", "Me");
        when(userRepository.findByEmail("me@x.com")).thenReturn(Optional.of(user));

        Notification n1 = mockNotification(
                1L,
                user,
                "Nova curtida",
                Notification.NotificationType.LIKE,
                false,
                LocalDateTime.now()
        );

        Notification n2 = mockNotification(
                2L,
                user,
                "Novo comentário",
                Notification.NotificationType.COMMENT,
                true,
                LocalDateTime.now()
        );

        when(notificationRepository.findByRecipientIdOrderByCreatedAtDesc(10L))
                .thenReturn(List.of(n1, n2));

        List<NotificationDto> dtos = service.getUserNotifications("me@x.com");

        assertEquals(2, dtos.size());

        NotificationDto d1 = dtos.get(0);
        assertEquals(1L, d1.getId());
        assertEquals("Nova curtida", d1.getMessage());
        assertEquals(Notification.NotificationType.LIKE, d1.getType());
        assertFalse(d1.isRead());
        assertEquals(10L, d1.getRecipientId());
        assertNotNull(d1.getTimestamp());

        NotificationDto d2 = dtos.get(1);
        assertEquals(2L, d2.getId());
        assertEquals("Novo comentário", d2.getMessage());
        assertEquals(Notification.NotificationType.COMMENT, d2.getType());
        assertTrue(d2.isRead());

        verify(notificationRepository).findByRecipientIdOrderByCreatedAtDesc(10L);
    }

    @Test
    @DisplayName("getUserNotifications: lança quando email não existe")
    void getUserNotifications_userNotFound() {
        when(userRepository.findByEmail("none@x.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.getUserNotifications("none@x.com")
        );

        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    @DisplayName("markAsRead: marca como lida e salva")
    void markAsRead_ok() {
        User user = mockUser(10L, "a@a.com", "A");
        Notification n = mockNotification(
                5L,
                user,
                "msg",
                Notification.NotificationType.MESSAGE,
                false,
                LocalDateTime.now()
        );

        when(notificationRepository.findById(5L)).thenReturn(Optional.of(n));

        service.markAsRead(5L);

        verify(n).setRead(true);
        verify(notificationRepository).save(n);
    }

    @Test
    @DisplayName("markAsRead: lança quando notificação não existe")
    void markAsRead_notFound() {
        when(notificationRepository.findById(404L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.markAsRead(404L)
        );

        assertTrue(ex.getMessage().contains("Notification not found"));
    }

    @Test
    @DisplayName("getUnreadCount: retorna quantidade correta")
    void getUnreadCount_ok() {
        User user = mockUser(10L, "me@x.com", "Me");
        when(userRepository.findByEmail("me@x.com")).thenReturn(Optional.of(user));
        when(notificationRepository.countByRecipientIdAndIsReadFalse(10L)).thenReturn(3L);

        long count = service.getUnreadCount("me@x.com");

        assertEquals(3L, count);
    }

    @Test
    @DisplayName("getUnreadCount: lança quando usuário não encontrado")
    void getUnreadCount_notFound() {
        when(userRepository.findByEmail("none@x.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.getUnreadCount("none@x.com")
        );

        assertTrue(ex.getMessage().contains("User not found"));
        verify(notificationRepository, never()).countByRecipientIdAndIsReadFalse(anyLong());
    }
}


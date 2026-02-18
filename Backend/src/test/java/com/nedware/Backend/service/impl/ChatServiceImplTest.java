package com.nedware.Backend.service.impl;


import com.nedware.Backend.domain.Message;
import com.nedware.Backend.domain.User;
import com.nedware.Backend.domain.dto.MessageDto;
import com.nedware.Backend.repository.MessageRepository;
import com.nedware.Backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ChatServiceImpl.class)
class ChatServiceImplTest {

    @Autowired
    private ChatServiceImpl service;

    @MockitoBean
    private MessageRepository messageRepository;

    @MockitoBean
    private UserRepository userRepository;

    // ------- helpers -------
    private User mockUser(long id, String email, String name, String avatar) {
        User u = mock(User.class);
        when(u.getId()).thenReturn(id);
        when(u.getEmail()).thenReturn(email);
        when(u.getName()).thenReturn(name);
        when(u.getAvatar()).thenReturn(avatar);
        return u;
    }

    private Message mockMessage(long id, User sender, User receiver, String content, LocalDateTime createdAt) {
        Message m = mock(Message.class);
        when(m.getId()).thenReturn(id);
        when(m.getSender()).thenReturn(sender);
        when(m.getReceiver()).thenReturn(receiver);
        when(m.getContent()).thenReturn(content);
        when(m.getCreatedAt()).thenReturn(createdAt);
        return m;
    }

    @Test
    @DisplayName("sendMessage: OK -> salva e mapeia todos os campos do DTO")
    void sendMessage_ok() {
        User sender = mockUser(10L, "alice@x.com", "Alice", "avaA.png");
        User receiver = mockUser(20L, "bob@x.com", "Bob", "avaB.png");
        Message saved = mockMessage(99L, sender, receiver, "hello", LocalDateTime.now());

        when(userRepository.findByEmail("alice@x.com")).thenReturn(Optional.of(sender));
        when(userRepository.findById(20L)).thenReturn(Optional.of(receiver));
        when(messageRepository.save(any(Message.class))).thenReturn(saved);

        MessageDto dto = service.sendMessage(20L, "hello", "alice@x.com");

        // valida mapeamento
        assertNotNull(dto);
        assertEquals(99L, dto.getId());
        assertEquals(10L, dto.getSenderId());
        assertEquals("Alice", dto.getSenderName());
        assertEquals("avaA.png", dto.getSenderAvatar());
        assertEquals(20L, dto.getReceiverId());
        assertEquals("Bob", dto.getReceiverName());
        assertEquals("avaB.png", dto.getReceiverAvatar());
        assertEquals("hello", dto.getContent());
        assertNotNull(dto.getTimestamp()); // proveniente de message.getCreatedAt() (BaseEntity) [1](https://certsysti-my.sharepoint.com/personal/matheus_silveira_certsys_com_br/Documents/Arquivos%20de%20Microsoft%20Copilot%20Chat/ChatServiceImpl.java)[3](https://certsysti-my.sharepoint.com/personal/matheus_silveira_certsys_com_br/Documents/Arquivos%20de%20Microsoft%20Copilot%20Chat/Message.java)

        // valida o objeto salvo
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(captor.capture());
        Message toSave = captor.getValue();
        assertSame(sender, toSave.getSender());
        assertSame(receiver, toSave.getReceiver());
        assertEquals("hello", toSave.getContent());
    }

    @Test
    @DisplayName("sendMessage: lança quando sender não encontrado")
    void sendMessage_senderNotFound() {
        when(userRepository.findByEmail("unknown@x.com")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.sendMessage(1L, "msg", "unknown@x.com"));
        assertTrue(ex.getMessage().contains("Sender not found")); // conforme implementação atual [1](https://certsysti-my.sharepoint.com/personal/matheus_silveira_certsys_com_br/Documents/Arquivos%20de%20Microsoft%20Copilot%20Chat/ChatServiceImpl.java)
        verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("sendMessage: lança quando receiver não encontrado")
    void sendMessage_receiverNotFound() {
        User sender = mockUser(10L, "alice@x.com", "Alice", "avaA.png");
        when(userRepository.findByEmail("alice@x.com")).thenReturn(Optional.of(sender));
        when(userRepository.findById(123L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.sendMessage(123L, "msg", "alice@x.com"));
        assertTrue(ex.getMessage().contains("Receiver not found")); // conforme implementação atual [1](https://certsysti-my.sharepoint.com/personal/matheus_silveira_certsys_com_br/Documents/Arquivos%20de%20Microsoft%20Copilot%20Chat/ChatServiceImpl.java)
        verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("getConversation: OK -> retorna lista mapeada")
    void getConversation_ok() {
        User current = mockUser(1L, "me@x.com", "Me", "avaMe.png");
        User other   = mockUser(2L, "you@x.com", "You", "avaYou.png");
        Message m1 = mockMessage(1L, current, other, "hi", LocalDateTime.now());
        Message m2 = mockMessage(2L, other, current, "hello", LocalDateTime.now());

        when(userRepository.findByEmail("me@x.com")).thenReturn(Optional.of(current));
        when(messageRepository.findConversation(1L, 2L)).thenReturn(Arrays.asList(m1, m2)); // chamada feita pelo service [1](https://certsysti-my.sharepoint.com/personal/matheus_silveira_certsys_com_br/Documents/Arquivos%20de%20Microsoft%20Copilot%20Chat/ChatServiceImpl.java)

        List<MessageDto> dtos = service.getConversation(2L, "me@x.com");

        assertEquals(2, dtos.size());
        assertEquals("hi", dtos.get(0).getContent());
        assertEquals("hello", dtos.get(1).getContent());
        verify(messageRepository).findConversation(1L, 2L);
    }

    @Test
    @DisplayName("getConversation: lança quando currentUser não encontrado")
    void getConversation_userNotFound() {
        when(userRepository.findByEmail("none@x.com")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getConversation(2L, "none@x.com"));
        assertTrue(ex.getMessage().contains("User not found")); // conforme implementação atual [1](https://certsysti-my.sharepoint.com/personal/matheus_silveira_certsys_com_br/Documents/Arquivos%20de%20Microsoft%20Copilot%20Chat/ChatServiceImpl.java)
        verify(messageRepository, never()).findConversation(anyLong(), anyLong());
    }
}


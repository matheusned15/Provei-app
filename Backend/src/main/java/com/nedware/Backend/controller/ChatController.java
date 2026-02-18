package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.MessageDto;
import com.nedware.Backend.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ResponseEntity<MessageDto> sendMessage(@RequestBody Map<String, Object> payload, Authentication authentication){
        Long receiverId = ((Number) payload.get("receiverId")).longValue();
        String content = (String) payload.get("content");
        String senderEmail = authentication.getName();

        return ResponseEntity.ok(chatService.sendMessage(receiverId, content, senderEmail));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<MessageDto>> getConversation(@PathVariable Long userId, Authentication authentication){
        String currentUserEmail = authentication.getName();
        return ResponseEntity.ok(chatService.getConversation(userId, currentUserEmail));
    }
}


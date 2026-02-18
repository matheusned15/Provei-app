package com.nedware.Backend.service;

import com.nedware.Backend.domain.dto.MessageDto;

import java.util.List;

public interface ChatService {
    MessageDto sendMessage(Long receiverId, String content, String senderEmail);
    List<MessageDto> getConversation(Long userId, String currentUserEmail);
}

package com.nedware.Backend.service.impl;

import com.nedware.Backend.domain.Message;
import com.nedware.Backend.domain.User;
import com.nedware.Backend.domain.dto.MessageDto;
import com.nedware.Backend.repository.MessageRepository;
import com.nedware.Backend.repository.UserRepository;
import com.nedware.Backend.service.ChatService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private MessageRepository messageRepository;
    private UserRepository userRepository;

    public ChatServiceImpl(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public MessageDto sendMessage(Long receiverId, String content, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);

        Message savedMessage = messageRepository.save(message);
        return mapToDto(savedMessage);
    }

    @Override
    @Transactional
    public List<MessageDto> getConversation(Long otherUserId, String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return messageRepository.findConversation(currentUser.getId(), otherUserId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private MessageDto mapToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getName());
        dto.setSenderAvatar(message.getSender().getAvatar());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setReceiverName(message.getReceiver().getName());
        dto.setReceiverAvatar(message.getReceiver().getAvatar());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getCreatedAt());
        return dto;
    }
}


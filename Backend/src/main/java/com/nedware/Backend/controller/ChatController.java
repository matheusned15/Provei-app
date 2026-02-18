package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.MessageDto;
import com.nedware.Backend.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Chat", description = "Mensagens privadas entre usuários (requer autenticação).")
@RestController
@RequestMapping("/api/chat")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }


    @Operation(
            summary = "Enviar mensagem",
            description = "Envia uma nova mensagem para um usuário específico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensagem enviada",
                    content = @Content(schema = @Schema(implementation = MessageDto.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Destinatário não encontrado")
    })
    @PostMapping("/send")
    public ResponseEntity<MessageDto> sendMessage(@RequestBody Map<String, Object> payload, Authentication authentication){
        Long receiverId = ((Number) payload.get("receiverId")).longValue();
        String content = (String) payload.get("content");
        String senderEmail = authentication.getName();

        return ResponseEntity.ok(chatService.sendMessage(receiverId, content, senderEmail));
    }


    @Operation(
            summary = "Obter conversa com um usuário",
            description = "Retorna a conversa (lista de mensagens) entre o usuário autenticado e o usuário indicado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conversa retornada",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MessageDto.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Conversa/usuário não encontrado")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<MessageDto>> getConversation(@PathVariable Long userId, Authentication authentication){
        String currentUserEmail = authentication.getName();
        return ResponseEntity.ok(chatService.getConversation(userId, currentUserEmail));
    }
}


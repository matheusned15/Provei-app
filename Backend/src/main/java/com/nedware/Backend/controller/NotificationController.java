package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.NotificationDto;
import com.nedware.Backend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notificações", description = "Gerenciamento de notificações do usuário (requer autenticação).")
@RestController
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @Operation(
            summary = "Listar notificações do usuário",
            description = "Retorna todas as notificações (lidas e não lidas) do usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de notificações retornada",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getUserNotifications(Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(notificationService.getUserNotifications(username));
    }


    @Operation(
            summary = "Marcar notificação como lida",
            description = "Marca uma notificação específica como lida."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificação marcada como lida",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Notification marked as read"))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    })
    @PostMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long id){
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Notification marked as read");
    }


    @Operation(
            summary = "Contar notificações não lidas",
            description = "Retorna a quantidade de notificações não lidas do usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contagem retornada",
                    content = @Content(schema = @Schema(implementation = Long.class),
                            examples = @ExampleObject(value = "5"))),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(notificationService.getUnreadCount(username));
    }
}

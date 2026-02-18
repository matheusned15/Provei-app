package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.CommentDto;
import com.nedware.Backend.service.SocialService;
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
import java.util.Map;

@Tag(name = "Interações sociais", description = "Curtidas e comentários em avaliações (requer autenticação).")
@RestController
@RequestMapping("/api/social")
@SecurityRequirement(name = "bearerAuth")
public class SocialController {

    private SocialService socialService;

    public SocialController(SocialService socialService) {
        this.socialService = socialService;
    }


    @Operation(
            summary = "Curtir ou remover curtida",
            description = "Alterna o estado de curtida de uma avaliação: se já possui curtida, remove; caso contrário, adiciona."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado de curtida atualizado",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    @PostMapping("/reviews/{reviewId}/like")
    public ResponseEntity<String> toggleLike(@PathVariable Long reviewId, Authentication authentication){
        String username = authentication.getName();
        boolean liked = socialService.toggleLike(reviewId, username);
        return ResponseEntity.ok(liked ? "Review liked" : "Review unliked");
    }

    @Operation(
            summary = "Comentar avaliação",
            description = "Adiciona um comentário à avaliação especificada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comentário criado",
                    content = @Content(schema = @Schema(implementation = CommentDto.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    @PostMapping("/reviews/{reviewId}/comment")
    public ResponseEntity<CommentDto> addComment(@PathVariable Long reviewId, @RequestBody Map<String, String> payload, Authentication authentication){
        String username = authentication.getName();
        String content = payload.get("content");
        return ResponseEntity.ok(socialService.addComment(reviewId, content, username));
    }

    @Operation(
            summary = "Listar comentários",
            description = "Retorna os comentários de uma avaliação."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comentários retornados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentDto.class))))
    })
    @GetMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long reviewId){
        return ResponseEntity.ok(socialService.getComments(reviewId));
    }

    @Operation(
            summary = "Contar curtidas",
            description = "Retorna o número de curtidas de uma avaliação."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contagem retornada",
                    content = @Content(schema = @Schema(implementation = Long.class),
                            examples = @ExampleObject(value = "12")))
    })
    @GetMapping("/reviews/{reviewId}/likes/count")
    public ResponseEntity<Long> getLikesCount(@PathVariable Long reviewId){
        return ResponseEntity.ok(socialService.getLikesCount(reviewId));
    }
}

package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.ReviewDto;
import com.nedware.Backend.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.web.PageableDefault;



@Tag(name = "Feed", description = "Feed de avaliações e atividades (requer autenticação).")
@RestController
@RequestMapping("/api/feed")
@SecurityRequirement(name = "bearerAuth")
public class FeedController {

    private FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }


    @Operation(
            summary = "Listar feed",
            description = "Retorna o feed paginado de avaliações/atividades relevantes para o usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feed retornado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping
    public ResponseEntity<Page<ReviewDto>> getFeed(@PageableDefault(size = 10) Pageable pageable){
        return ResponseEntity.ok(feedService.getFeedPosts(pageable));
    }
}
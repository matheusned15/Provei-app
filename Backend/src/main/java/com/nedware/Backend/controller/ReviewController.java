package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.ReviewDto;
import com.nedware.Backend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Avaliações", description = "Criação e listagem de avaliações de pratos (requer autenticação).")
@RestController
@RequestMapping("/api/reviews")
@SecurityRequirement(name = "bearerAuth")
public class ReviewController {

    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Avaliação criada",
                    content = @Content(schema = @Schema(implementation = ReviewDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Prato não encontrado")
    })
    @PostMapping("/dish/{dishId}")
    public ResponseEntity<ReviewDto> addReview(@PathVariable Long dishId,
                                               @RequestBody ReviewDto reviewDto,
                                               Authentication authentication){
        String username = authentication.getName();
        return new ResponseEntity<>(reviewService.addReview(dishId, reviewDto, username), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Listar avaliações de um prato",
            description = "Retorna todas as avaliações associadas a um prato."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avaliações encontradas",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewDto.class)))),
            @ApiResponse(responseCode = "404", description = "Prato não encontrado")
    })
    @GetMapping("/dish/{dishId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByDish(@PathVariable Long dishId){
        return ResponseEntity.ok(reviewService.getReviewsByDishId(dishId));
    }
}

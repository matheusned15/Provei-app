package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.DishDto;
import com.nedware.Backend.service.WishlistService;
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

@Tag(name = "Lista de desejos", description = "Gerenciamento da wishlist do usuário (requer autenticação).")
@RestController
@RequestMapping("/api/wishlist")
@SecurityRequirement(name = "bearerAuth")
public class WishlistController {

    private WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }


    @Operation(
            summary = "Obter wishlist do usuário",
            description = "Retorna a lista de pratos adicionados à wishlist do usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wishlist retornada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DishDto.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping
    public ResponseEntity<List<DishDto>> getWishlist(Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(wishlistService.getUserWishlist(username));
    }


    @Operation(
            summary = "Adicionar prato à wishlist",
            description = "Adiciona o prato informado à wishlist do usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prato adicionado à wishlist",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Added to wishlist"))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Prato não encontrado")
    })
    @PostMapping("/{dishId}")
    public ResponseEntity<String> addToWishlist(@PathVariable Long dishId, Authentication authentication){
        String username = authentication.getName();
        wishlistService.addToWishlist(dishId, username);
        return ResponseEntity.ok("Added to wishlist");
    }


    @Operation(
            summary = "Remover prato da wishlist",
            description = "Remove o prato informado da wishlist do usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prato removido da wishlist",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Removed from wishlist"))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Prato não encontrado")
    })
    @DeleteMapping("/{dishId}")
    public ResponseEntity<String> removeFromWishlist(@PathVariable Long dishId, Authentication authentication){
        String username = authentication.getName();
        wishlistService.removeFromWishlist(dishId, username);
        return ResponseEntity.ok("Removed from wishlist");
    }
}

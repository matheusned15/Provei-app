package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.UserProfileDto;
import com.nedware.Backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuários", description = "Acesso ao perfil e interações entre usuários (requer autenticação).")
@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Buscar perfil de um usuário",
            description = "Retorna o perfil público de um usuário pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado",
                    content = @Content(schema = @Schema(implementation = UserProfileDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @Operation(
            summary = "Perfil do usuário autenticado",
            description = "Retorna o perfil do usuário atual, baseado no token JWT enviado."
    )
    @ApiResponse(responseCode = "200", description = "Perfil retornado",
            content = @Content(schema = @Schema(implementation = UserProfileDto.class)))
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUser(Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getCurrentUserProfile(username));
    }

    @Operation(
            summary = "Seguir usuário",
            description = "Permite que o usuário autenticado siga outro usuário."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário seguido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PostMapping("/{id}/follow")
    public ResponseEntity<String> followUser(@PathVariable Long id, Authentication authentication){
        String username = authentication.getName();
        userService.followUser(id, username);
        return ResponseEntity.ok("User followed successfully");
    }
}

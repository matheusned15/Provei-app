package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.AuthResponseDto;
import com.nedware.Backend.domain.dto.LoginDto;
import com.nedware.Backend.domain.dto.RegisterDto;
import com.nedware.Backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Endpoints de autenticação (públicos): login e cadastro de usuários.")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @Operation(
            summary = "Login",
            description = "Autentica o usuário com e‑mail e senha e retorna um token JWT de acesso."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
        String token = authService.login(loginDto);

        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setAccessToken(token);

        return ResponseEntity.ok(authResponseDto);
    }


    @Operation(
            summary = "Cadastro",
            description = "Cria um novo usuário no sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "409", description = "E‑mail já cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        String response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

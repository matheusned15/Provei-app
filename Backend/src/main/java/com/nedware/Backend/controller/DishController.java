package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.DishDto;
import com.nedware.Backend.service.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Pratos", description = "Consulta pública de pratos.")
@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }


    @Operation(
            summary = "Buscar prato por ID",
            description = "Recupera os detalhes de um prato específico pelo seu identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prato encontrado",
                    content = @Content(schema = @Schema(implementation = DishDto.class))),
            @ApiResponse(responseCode = "404", description = "Prato não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DishDto> getDishById(@PathVariable Long id){
        return ResponseEntity.ok(dishService.getDishById(id));
    }


    @Operation(
            summary = "Pesquisar pratos",
            description = "Pesquisa pratos pelo nome/termos livres."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados retornados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DishDto.class))))
    })
    @GetMapping("/search")
    public ResponseEntity<List<DishDto>> searchDishes(@RequestParam String query){
        return ResponseEntity.ok(dishService.searchDishes(query));
    }
}


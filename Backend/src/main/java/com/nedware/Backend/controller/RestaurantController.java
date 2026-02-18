package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.RestaurantDto;
import com.nedware.Backend.service.RestaurantService;
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


@Tag(name = "Restaurantes", description = "Consulta pública de restaurantes.")
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }


    @Operation(
            summary = "Listar restaurantes",
            description = "Retorna a lista de restaurantes. Pode filtrar por termo de busca (nome, descrição, etc.)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantDto.class))))
    })
    @GetMapping
    public ResponseEntity<List<RestaurantDto>> getAllRestaurants(@RequestParam(required = false) String search){
        return ResponseEntity.ok(restaurantService.getAllRestaurants(search));
    }


    @Operation(
            summary = "Buscar restaurantes próximos",
            description = "Retorna restaurantes próximos às coordenadas informadas, dentro de um raio em quilômetros."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de restaurantes próximos",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantDto.class)))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos (lat/lon)")
    })
    @GetMapping("/near")
    public ResponseEntity<List<RestaurantDto>> getRestaurantsNear(@RequestParam double lat,
                                                                  @RequestParam double lon,
                                                                  @RequestParam(defaultValue = "5.0") double distanceKm){
        return ResponseEntity.ok(restaurantService.findNearRestaurants(lat, lon, distanceKm));
    }


    @Operation(
            summary = "Buscar restaurante por ID",
            description = "Retorna os detalhes de um restaurante pelo seu identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado",
                    content = @Content(schema = @Schema(implementation = RestaurantDto.class))),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDto> getRestaurantById(@PathVariable Long id){
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }
}

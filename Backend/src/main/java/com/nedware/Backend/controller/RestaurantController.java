package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.RestaurantDto;
import com.nedware.Backend.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public ResponseEntity<List<RestaurantDto>> getAllRestaurants(@RequestParam(required = false) String search){
        return ResponseEntity.ok(restaurantService.getAllRestaurants(search));
    }

    @GetMapping("/near")
    public ResponseEntity<List<RestaurantDto>> getRestaurantsNear(@RequestParam double lat,
                                                                  @RequestParam double lon,
                                                                  @RequestParam(defaultValue = "5.0") double distanceKm){
        return ResponseEntity.ok(restaurantService.findNearRestaurants(lat, lon, distanceKm));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDto> getRestaurantById(@PathVariable Long id){
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }
}

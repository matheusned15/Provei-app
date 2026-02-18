package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.DishDto;
import com.nedware.Backend.service.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DishDto> getDishById(@PathVariable Long id){
        return ResponseEntity.ok(dishService.getDishById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DishDto>> searchDishes(@RequestParam String query){
        return ResponseEntity.ok(dishService.searchDishes(query));
    }
}


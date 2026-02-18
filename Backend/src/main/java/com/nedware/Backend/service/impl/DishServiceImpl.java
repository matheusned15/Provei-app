package com.nedware.Backend.service.impl;

import com.nedware.Backend.domain.Dish;
import com.nedware.Backend.domain.dto.DishDto;
import com.nedware.Backend.repository.DishRepository;
import com.nedware.Backend.service.DishService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl implements DishService {

    private DishRepository dishRepository;

    public DishServiceImpl(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DishDto getDishById(Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found"));
        return mapToDto(dish);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DishDto> searchDishes(String query) {
        List<Dish> dishes = dishRepository.findByNameContainingIgnoreCase(query);
        return dishes.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private DishDto mapToDto(Dish dish) {
        DishDto dto = new DishDto();
        dto.setId(dish.getId());
        dto.setName(dish.getName());
        dto.setPrice(dish.getPrice());
        dto.setDescription(dish.getDescription());
        dto.setPhotoUrl(dish.getPhotoUrl());
        dto.setAverageRating(dish.getAverageRating());
        if(dish.getRestaurant() != null) {
            dto.setRestaurantId(dish.getRestaurant().getId());
            dto.setRestaurantName(dish.getRestaurant().getName());
        }
        return dto;
    }
}

package com.nedware.Backend.service;

import com.nedware.Backend.domain.dto.DishDto;

import java.util.List;

public interface DishService {
    DishDto getDishById(Long id);
    List<DishDto> searchDishes(String query);
}

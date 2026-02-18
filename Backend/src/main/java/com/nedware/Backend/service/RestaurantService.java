package com.nedware.Backend.service;

import com.nedware.Backend.domain.dto.RestaurantDto;

import java.util.List;

public interface RestaurantService {
    List<RestaurantDto> getAllRestaurants(String search);
    RestaurantDto getRestaurantById(Long id);
    List<RestaurantDto> findNearRestaurants(double lat, double lon, double distanceKm);
}


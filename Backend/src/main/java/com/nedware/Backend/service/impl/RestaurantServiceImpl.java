package com.nedware.Backend.service.impl;

import com.nedware.Backend.domain.Dish;
import com.nedware.Backend.domain.Restaurant;
import com.nedware.Backend.domain.dto.DishDto;
import com.nedware.Backend.domain.dto.RestaurantDto;
import com.nedware.Backend.repository.RestaurantRepository;
import com.nedware.Backend.service.RestaurantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private RestaurantRepository restaurantRepository;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantDto> getAllRestaurants(String search) {
        List<Restaurant> restaurants;
        if (search != null && !search.isEmpty()) {
            restaurants = restaurantRepository.findByNameContainingIgnoreCase(search);
        } else {
            restaurants = restaurantRepository.findAll();
        }
        return restaurants.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantDto> findNearRestaurants(double lat, double lon, double distanceKm) {
        List<Restaurant> restaurants = restaurantRepository.findNear(lat, lon, distanceKm);
        return restaurants.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantDto getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        return mapToDto(restaurant);
    }

    private RestaurantDto mapToDto(Restaurant restaurant) {
        RestaurantDto dto = new RestaurantDto();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setDescription(restaurant.getDescription());
        dto.setAddress(restaurant.getAddress());
        dto.setLatitude(restaurant.getLatitude());
        dto.setLongitude(restaurant.getLongitude());
        dto.setCoverImage(restaurant.getCoverImage());
        dto.setRating(restaurant.getRating());

        if (restaurant.getDishes() != null) {
            dto.setDishes(restaurant.getDishes().stream().map(this::mapDishToDto).collect(Collectors.toList()));
        }

        return dto;
    }

    private DishDto mapDishToDto(Dish dish) {
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


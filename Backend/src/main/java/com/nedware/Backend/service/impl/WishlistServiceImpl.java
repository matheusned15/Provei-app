package com.nedware.Backend.service.impl;

import com.nedware.Backend.domain.Dish;
import com.nedware.Backend.domain.User;
import com.nedware.Backend.domain.WishlistItem;
import com.nedware.Backend.domain.dto.DishDto;
import com.nedware.Backend.repository.DishRepository;
import com.nedware.Backend.repository.UserRepository;
import com.nedware.Backend.repository.WishlistItemRepository;
import com.nedware.Backend.service.WishlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    private WishlistItemRepository wishlistRepository;
    private UserRepository userRepository;
    private DishRepository dishRepository;

    public WishlistServiceImpl(WishlistItemRepository wishlistRepository, UserRepository userRepository, DishRepository dishRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.dishRepository = dishRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DishDto> getUserWishlist(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<WishlistItem> items = wishlistRepository.findByUserId(user.getId());

        return items.stream()
                .map(item -> mapDishToDto(item.getDish()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addToWishlist(Long dishId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new RuntimeException("Dish not found"));

        if (wishlistRepository.findByUserIdAndDishId(user.getId(), dishId).isPresent()) {
            return; // Already in wishlist
        }

        WishlistItem item = new WishlistItem();
        item.setUser(user);
        item.setDish(dish);
        wishlistRepository.save(item);
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long dishId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        wishlistRepository.deleteByUserIdAndDishId(user.getId(), dishId);
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

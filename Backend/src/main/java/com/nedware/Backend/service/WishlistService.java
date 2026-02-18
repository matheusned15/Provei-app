package com.nedware.Backend.service;

import com.nedware.Backend.domain.dto.DishDto;

import java.util.List;

public interface WishlistService {
    List<DishDto> getUserWishlist(String username);
    void addToWishlist(Long dishId, String username);
    void removeFromWishlist(Long dishId, String username);
}


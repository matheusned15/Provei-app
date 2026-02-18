package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.DishDto;
import com.nedware.Backend.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<List<DishDto>> getWishlist(Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(wishlistService.getUserWishlist(username));
    }

    @PostMapping("/{dishId}")
    public ResponseEntity<String> addToWishlist(@PathVariable Long dishId, Authentication authentication){
        String username = authentication.getName();
        wishlistService.addToWishlist(dishId, username);
        return ResponseEntity.ok("Added to wishlist");
    }

    @DeleteMapping("/{dishId}")
    public ResponseEntity<String> removeFromWishlist(@PathVariable Long dishId, Authentication authentication){
        String username = authentication.getName();
        wishlistService.removeFromWishlist(dishId, username);
        return ResponseEntity.ok("Removed from wishlist");
    }
}

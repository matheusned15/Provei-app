package com.nedware.Backend.service.impl;

import com.nedware.Backend.domain.Dish;
import com.nedware.Backend.domain.User;
import com.nedware.Backend.domain.WishlistItem;
import com.nedware.Backend.domain.dto.DishDto;
import com.nedware.Backend.repository.DishRepository;
import com.nedware.Backend.repository.UserRepository;
import com.nedware.Backend.repository.WishlistItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceImplTest {

    @Mock
    private WishlistItemRepository wishlistRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    @Test
    void getUserWishlist_returnsDishDtos() {
        User u = new User(); u.setId(2L); u.setEmail("u@x");
        Dish d = new Dish(); d.setId(3L); d.setName("Sushi"); d.setPrice(new BigDecimal("20.00"));
        WishlistItem item = new WishlistItem(); item.setId(1L); item.setUser(u); item.setDish(d);

        when(userRepository.findByEmail("u@x")).thenReturn(Optional.of(u));
        when(wishlistRepository.findByUserId(2L)).thenReturn(List.of(item));

        List<DishDto> res = wishlistService.getUserWishlist("u@x");
        assertEquals(1, res.size());
        assertEquals(3L, res.get(0).getId());
    }

    @Test
    void addToWishlist_savesWhenNotExists() {
        User u = new User(); u.setId(2L); u.setEmail("u@x");
        Dish d = new Dish(); d.setId(3L);

        when(userRepository.findByEmail("u@x")).thenReturn(Optional.of(u));
        when(dishRepository.findById(3L)).thenReturn(Optional.of(d));
        when(wishlistRepository.findByUserIdAndDishId(2L, 3L)).thenReturn(Optional.empty());

        wishlistService.addToWishlist(3L, "u@x");

        verify(wishlistRepository).save(any(WishlistItem.class));
    }

    @Test
    void addToWishlist_noDuplicate_whenAlreadyExists() {
        User u = new User(); u.setId(2L); u.setEmail("u@x");
        Dish d = new Dish(); d.setId(3L);

        when(userRepository.findByEmail("u@x")).thenReturn(Optional.of(u));
        when(dishRepository.findById(3L)).thenReturn(Optional.of(d));
        when(wishlistRepository.findByUserIdAndDishId(2L, 3L)).thenReturn(Optional.of(new WishlistItem()));

        wishlistService.addToWishlist(3L, "u@x");

        verify(wishlistRepository, never()).save(any(WishlistItem.class));
    }

    @Test
    void removeFromWishlist_callsDelete() {
        User u = new User(); u.setId(2L); u.setEmail("u@x");
        when(userRepository.findByEmail("u@x")).thenReturn(Optional.of(u));

        wishlistService.removeFromWishlist(3L, "u@x");

        verify(wishlistRepository).deleteByUserIdAndDishId(2L, 3L);
    }
}


package com.nedware.Backend.service.impl;

import com.nedware.Backend.domain.Dish;
import com.nedware.Backend.domain.Review;
import com.nedware.Backend.domain.User;
import com.nedware.Backend.domain.dto.ReviewDto;
import com.nedware.Backend.repository.DishRepository;
import com.nedware.Backend.repository.ReviewRepository;
import com.nedware.Backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void addReview_success_updatesDishRating_and_returnsDto() {
        Dish dish = new Dish();
        dish.setId(1L);
        dish.setAverageRating(0.0);

        User user = new User();
        user.setId(2L);
        user.setName("Alice");
        user.setAvatar("avatar.png");

        ReviewDto input = new ReviewDto();
        input.setRating(5);
        input.setComment("Great");
        input.setPhotoUrl("p.jpg");

        Review saved = new Review();
        saved.setId(10L);
        saved.setRating(5);
        saved.setComment("Great");
        saved.setPhotoUrl("p.jpg");
        saved.setDish(dish);
        saved.setUser(user);
        saved.setCreatedAt(LocalDateTime.now());

        when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(Review.class))).thenReturn(saved);
        // For updateDishRating: return the saved review as the only review
        when(reviewRepository.findByDishId(1L)).thenReturn(List.of(saved));
        when(dishRepository.save(any(Dish.class))).thenReturn(dish);

        ReviewDto result = reviewService.addReview(1L, input, "alice@example.com");

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(5, result.getRating());
        assertEquals("Great", result.getComment());
        assertEquals(1L, result.getDishId());
        assertEquals(2L, result.getUserId());
        assertEquals("Alice", result.getUserName());
        assertEquals("avatar.png", result.getUserAvatar());

        // Verify dish rating was recalculated and saved
        ArgumentCaptor<Dish> dishCaptor = ArgumentCaptor.forClass(Dish.class);
        verify(dishRepository).save(dishCaptor.capture());
        Dish savedDish = dishCaptor.getValue();
        assertEquals(5.0, savedDish.getAverageRating());
    }

    @Test
    void addReview_dishNotFound_throws() {
        when(dishRepository.findById(99L)).thenReturn(Optional.empty());
        ReviewDto input = new ReviewDto();
        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.addReview(99L, input, "x@x"));
        assertTrue(ex.getMessage().contains("Dish not found"));
    }

    @Test
    void addReview_userNotFound_throws() {
        Dish dish = new Dish(); dish.setId(1L);
        when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
        when(userRepository.findByEmail("noone@example.com")).thenReturn(Optional.empty());
        ReviewDto input = new ReviewDto();
        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.addReview(1L, input, "noone@example.com"));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void getReviewsByDishId_mapsToDtoList() {
        Dish dish = new Dish(); dish.setId(1L);
        User user = new User(); user.setId(2L); user.setName("B"); user.setAvatar("a.png");
        Review r = new Review(); r.setId(5L); r.setRating(4); r.setComment("ok"); r.setDish(dish); r.setUser(user);
        when(reviewRepository.findByDishId(1L)).thenReturn(List.of(r));

        List<com.nedware.Backend.domain.dto.ReviewDto> dtos = reviewService.getReviewsByDishId(1L);
        assertEquals(1, dtos.size());
        com.nedware.Backend.domain.dto.ReviewDto dto = dtos.get(0);
        assertEquals(5L, dto.getId());
        assertEquals(4, dto.getRating());
        assertEquals(1L, dto.getDishId());
        assertEquals(2L, dto.getUserId());
    }
}

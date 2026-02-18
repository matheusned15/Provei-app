package com.nedware.Backend.service.impl;


import com.nedware.Backend.domain.Review;
import com.nedware.Backend.domain.User;
import com.nedware.Backend.domain.Dish;
import com.nedware.Backend.domain.dto.ReviewDto;
import com.nedware.Backend.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = FeedServiceImpl.class)
class FeedServiceImplTest {

    @Autowired
    private FeedServiceImpl service;

    @MockitoBean
    private ReviewRepository reviewRepository;

    // -------- HELPERS --------

    private User mockUser(Long id, String name, String avatar) {
        User u = mock(User.class);
        when(u.getId()).thenReturn(id);
        when(u.getName()).thenReturn(name);
        when(u.getAvatar()).thenReturn(avatar);
        return u;
    }

    private Dish mockDish(Long id) {
        Dish d = mock(Dish.class);
        when(d.getId()).thenReturn(id);
        return d;
    }

    private Review mockReview(
            Long id,
            Integer rating,
            String comment,
            String photoUrl,
            LocalDateTime createdAt,
            User user,
            Dish dish
    ) {
        Review r = mock(Review.class);
        when(r.getId()).thenReturn(id);
        when(r.getRating()).thenReturn(rating);
        when(r.getComment()).thenReturn(comment);
        when(r.getPhotoUrl()).thenReturn(photoUrl);
        when(r.getCreatedAt()).thenReturn(createdAt);
        when(r.getUser()).thenReturn(user);
        when(r.getDish()).thenReturn(dish);
        return r;
    }

    // ------------------------- TESTES -------------------------

    @Test
    @DisplayName("getFeedPosts: retorna página com posts mapeados corretamente")
    void getFeedPosts_ok() {
        User user = mockUser(100L, "Alice", "ava.png");
        Dish dish = mockDish(200L);

        Review review = mockReview(
                1L,
                5,
                "Excelente!",
                "foto.png",
                LocalDateTime.now(),
                user,
                dish
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> page = new PageImpl<>(List.of(review), pageable, 1);

        when(reviewRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(page);

        Page<ReviewDto> result = service.getFeedPosts(pageable);

        assertEquals(1, result.getTotalElements());
        ReviewDto dto = result.getContent().get(0);

        // validações do mapping (conforme mapToDto no arquivo real) [1](https://certsysti-my.sharepoint.com/personal/matheus_silveira_certsys_com_br/Documents/Arquivos%20de%20Microsoft%20Copilot%20Chat/FeedServiceImpl.java)
        assertEquals(1L, dto.getId());
        assertEquals(5, dto.getRating());
        assertEquals("Excelente!", dto.getComment());
        assertEquals("foto.png", dto.getPhotoUrl());
        assertEquals(dish.getId(), dto.getDishId());
        assertEquals(user.getId(), dto.getUserId());
        assertEquals(user.getName(), dto.getUserName());
        assertEquals(user.getAvatar(), dto.getUserAvatar());
        assertNotNull(dto.getCreatedAt());

        verify(reviewRepository).findAllByOrderByCreatedAtDesc(pageable);
    }

    @Test
    @DisplayName("getFeedPosts: usuário null no review → branch do mapToDto sem user")
    void getFeedPosts_userNull() {
        Dish dish = mockDish(200L);

        Review review = mockReview(
                1L,
                4,
                "Ok",
                null,
                LocalDateTime.now(),
                null,      // USER NULL → testa o ramo condicional do mapToDto [1](https://certsysti-my.sharepoint.com/personal/matheus_silveira_certsys_com_br/Documents/Arquivos%20de%20Microsoft%20Copilot%20Chat/FeedServiceImpl.java)
                dish
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> page = new PageImpl<>(List.of(review), pageable, 1);

        when(reviewRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(page);

        Page<ReviewDto> result = service.getFeedPosts(pageable);

        ReviewDto dto = result.getContent().get(0);

        assertEquals(1L, dto.getId());
        assertEquals(4, dto.getRating());
        assertEquals("Ok", dto.getComment());
        assertEquals(200L, dto.getDishId());

        // Campos USER devem ficar null
        assertNull(dto.getUserId());
        assertNull(dto.getUserName());
        assertNull(dto.getUserAvatar());
    }

    @Test
    @DisplayName("getFeedPosts: retorna página vazia corretamente")
    void getFeedPosts_emptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(reviewRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(page);

        Page<ReviewDto> result = service.getFeedPosts(pageable);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }
}


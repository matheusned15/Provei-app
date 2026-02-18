package com.nedware.Backend.service.impl;

import com.nedware.Backend.domain.Dish;
import com.nedware.Backend.domain.Restaurant;
import com.nedware.Backend.domain.dto.DishDto;
import com.nedware.Backend.repository.DishRepository;
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
class DishServiceImplTest {

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private DishServiceImpl dishService;

    @Test
    void getDishById_returnsDto() {
        Dish d = new Dish();
        d.setId(3L);
        d.setName("Pasta");
        d.setPrice(new BigDecimal("12.50"));
        d.setDescription("Nice");
        d.setPhotoUrl("p.jpg");
        d.setAverageRating(4.2);
        Restaurant r = new Restaurant(); r.setId(7L); r.setName("R1");
        d.setRestaurant(r);

        when(dishRepository.findById(3L)).thenReturn(Optional.of(d));

        DishDto dto = dishService.getDishById(3L);
        assertEquals(3L, dto.getId());
        assertEquals("Pasta", dto.getName());
        assertEquals(new BigDecimal("12.50"), dto.getPrice());
        assertEquals(4.2, dto.getAverageRating());
        assertEquals(7L, dto.getRestaurantId());
        assertEquals("R1", dto.getRestaurantName());
    }

    @Test
    void getDishById_notFound_throws() {
        when(dishRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> dishService.getDishById(99L));
        assertTrue(ex.getMessage().contains("Dish not found"));
    }

    @Test
    void searchDishes_mapsResults() {
        Dish a = new Dish(); a.setId(1L); a.setName("Pizza");
        Dish b = new Dish(); b.setId(2L); b.setName("Pizzetta");
        when(dishRepository.findByNameContainingIgnoreCase("piz")).thenReturn(List.of(a, b));

        List<DishDto> res = dishService.searchDishes("piz");
        assertEquals(2, res.size());
        assertEquals(1L, res.get(0).getId());
    }
}


package com.nedware.Backend.service.impl;

import com.nedware.Backend.domain.Dish;
import com.nedware.Backend.domain.Restaurant;
import com.nedware.Backend.domain.dto.RestaurantDto;
import com.nedware.Backend.domain.dto.DishDto;
import com.nedware.Backend.repository.RestaurantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = RestaurantServiceImpl.class)
class RestaurantServiceImplTest {

    @Autowired
    private RestaurantServiceImpl service;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    // ----------- HELPERS -----------

    private Dish mockDish(Long id, String name, double price, String desc, String photo, double rating, Restaurant restaurant) {
        Dish d = mock(Dish.class);
        when(d.getId()).thenReturn(id);
        when(d.getName()).thenReturn(name);
        when(d.getPrice()).thenReturn(BigDecimal.valueOf(price));
        when(d.getDescription()).thenReturn(desc);
        when(d.getPhotoUrl()).thenReturn(photo);
        when(d.getAverageRating()).thenReturn(rating);
        when(d.getRestaurant()).thenReturn(restaurant);
        return d;
    }

    private Restaurant mockRestaurant(
            Long id,
            String name,
            String description,
            String address,
            Double lat,
            Double lon,
            String cover,
            Double rating,
            List<Dish> dishes
    ) {
        Restaurant r = mock(Restaurant.class);
        when(r.getId()).thenReturn(id);
        when(r.getName()).thenReturn(name);
        when(r.getDescription()).thenReturn(description);
        when(r.getAddress()).thenReturn(address);
        when(r.getLatitude()).thenReturn(lat);
        when(r.getLongitude()).thenReturn(lon);
        when(r.getCoverImage()).thenReturn(cover);
        when(r.getRating()).thenReturn(rating);
        when(r.getDishes()).thenReturn(dishes);
        return r;
    }

    // ---------- TESTES ----------

    @Test
    @DisplayName("getAllRestaurants: sem search → retorna todos e mapeia corretamente")
    void getAllRestaurants_noSearch() {
        Restaurant r1 = mockRestaurant(1L, "Rest A", "Desc A", "Addr A", 10.0, 20.0, "cov1.png", 4.5, new ArrayList<>());

        when(restaurantRepository.findAll()).thenReturn(List.of(r1));

        List<RestaurantDto> result = service.getAllRestaurants(null);

        assertEquals(1, result.size());
        RestaurantDto dto = result.get(0);

        assertEquals(1L, dto.getId());
        assertEquals("Rest A", dto.getName());
        assertEquals("Desc A", dto.getDescription());
        assertEquals("Addr A", dto.getAddress());
        assertEquals(10.0, dto.getLatitude());
        assertEquals(20.0, dto.getLongitude());
        assertEquals("cov1.png", dto.getCoverImage());
        assertEquals(4.5, dto.getRating());

        verify(restaurantRepository).findAll();
    }

    @Test
    @DisplayName("getAllRestaurants: com search → chama findByNameContainingIgnoreCase e mapeia")
    void getAllRestaurants_withSearch() {
        Restaurant r1 = mockRestaurant(2L, "Burger House", "Burgers", "Addr B", 11.0, 22.0, "cov2.png", 4.0, new ArrayList<>());

        when(restaurantRepository.findByNameContainingIgnoreCase("burg")).thenReturn(List.of(r1));

        List<RestaurantDto> result = service.getAllRestaurants("burg");

        assertEquals(1, result.size());
        assertEquals("Burger House", result.get(0).getName());

        verify(restaurantRepository).findByNameContainingIgnoreCase("burg");
    }

    @Test
    @DisplayName("findNearRestaurants: retorna lista mapeada corretamente")
    void findNearRestaurants_ok() {
        Restaurant r = mockRestaurant(3L, "Pizza Hut", "Pizza desc", "Addr C", 30.0, 40.0, "cov3.png", 4.7, new ArrayList<>());

        when(restaurantRepository.findNear(30.0, 40.0, 5.0)).thenReturn(List.of(r));

        List<RestaurantDto> result = service.findNearRestaurants(30.0, 40.0, 5.0);

        assertEquals(1, result.size());
        assertEquals("Pizza Hut", result.get(0).getName());

        verify(restaurantRepository).findNear(30.0, 40.0, 5.0);
    }

    @Test
    @DisplayName("getRestaurantById: retorna DTO quando existe")
    void getRestaurantById_ok() {
        Restaurant r = mockRestaurant(10L, "KFC", "Fried chicken", "Addr D", 1.1, 2.2, "cov4.png", 4.2, new ArrayList<>());

        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(r));

        RestaurantDto dto = service.getRestaurantById(10L);

        assertEquals("KFC", dto.getName());
        assertEquals(4.2, dto.getRating());

        verify(restaurantRepository).findById(10L);
    }

    @Test
    @DisplayName("getRestaurantById: lança exceção quando restaurante não existe")
    void getRestaurantById_notFound() {
        when(restaurantRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.getRestaurantById(999L)
        );

        assertTrue(ex.getMessage().contains("Restaurant not found")); // exatamente como no código real

        verify(restaurantRepository).findById(999L);
    }

    @Test
    @DisplayName("mapToDto: mapeia dishes → incluindo restaurantId e restaurantName")
    void mapToDto_withDishes() {
        Restaurant parent = mockRestaurant(20L, "Big Boss", "Desc", "Addr", 50.0, 51.0, "cov.png", 3.9, new ArrayList<>());

        Dish d1 = mockDish(100L, "Dish1", 30.0, "d1", "p1.png", 4.1, parent);
        Dish d2 = mockDish(101L, "Dish2", 35.0, "d2", "p2.png", 4.4, parent);

        when(parent.getDishes()).thenReturn(List.of(d1, d2));

        when(restaurantRepository.findById(20L)).thenReturn(Optional.of(parent));

        RestaurantDto dto = service.getRestaurantById(20L);

        assertNotNull(dto.getDishes());
        assertEquals(2, dto.getDishes().size());

        DishDto dd = dto.getDishes().get(0);
        assertEquals(100L, dd.getId());
        assertEquals("Dish1", dd.getName());
        assertEquals(30.0, dd.getPrice());
        assertEquals("p1.png", dd.getPhotoUrl());
        assertEquals(4.1, dd.getAverageRating());
        assertEquals(20L, dd.getRestaurantId());
        assertEquals("Big Boss", dd.getRestaurantName());
    }
}

package com.nedware.Backend.repository;

import com.nedware.Backend.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByNameContainingIgnoreCase(String name);

    @Query(value = "SELECT r.* FROM restaurants r WHERE " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(r.latitude)) * " +
            "cos(radians(r.longitude) - radians(:lon)) + " +
            "sin(radians(:lat)) * sin(radians(r.latitude)))) < :distanceKm",
            nativeQuery = true)
    List<Restaurant> findNear(@Param("lat") double lat, @Param("lon") double lon, @Param("distanceKm") double distanceKm);
}


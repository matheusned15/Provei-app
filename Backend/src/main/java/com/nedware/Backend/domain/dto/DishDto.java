package com.nedware.Backend.domain.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DishDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String photoUrl;
    private Double averageRating;
    private Long restaurantId;
    private String restaurantName;
}

package com.nedware.Backend.domain.dto;

import lombok.Data;
import java.util.List;

@Data
public class RestaurantDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
    private String coverImage;
    private Double rating;
    private List<DishDto> dishes;
}

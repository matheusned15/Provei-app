package com.nedware.Backend.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "restaurants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Restaurant extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
    private String coverImage;
    private Double rating; // Aggregate rating

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Dish> dishes = new ArrayList<>();
}


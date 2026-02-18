package com.nedware.Backend.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "dishes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Dish extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private BigDecimal price;
    private String description;
    private String photoUrl;
    private Double averageRating;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore // Prevent infinite recursion in simple serialization
    private Restaurant restaurant;

    @OneToMany(mappedBy = "dish", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();
}

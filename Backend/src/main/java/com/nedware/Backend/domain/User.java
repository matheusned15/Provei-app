package com.nedware.Backend.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String avatar;
    private String bio;

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN, USER, PARTNER

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<WishlistItem> wishlist = new ArrayList<>();

    // Simplification for followers/following - could be ManyToMany self join
    @ElementCollection
    private List<Long> followingIds = new ArrayList<>();

    @ElementCollection
    private List<Long> followerIds = new ArrayList<>();

    public enum Role {
        USER, ADMIN, PARTNER
    }
}


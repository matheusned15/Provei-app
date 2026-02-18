package com.nedware.Backend.domain.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserProfileDto {
    private Long id;
    private String name;
    private String email;
    private String avatar;
    private String bio;
    private int followersCount;
    private int followingCount;
    private List<ReviewDto> recentReviews;
}

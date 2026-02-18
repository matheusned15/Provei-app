package com.nedware.Backend.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDto {
    private Long id;
    private Integer rating;
    private String comment;
    private String photoUrl;
    private LocalDateTime createdAt;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long dishId;
}

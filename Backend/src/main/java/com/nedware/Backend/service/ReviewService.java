package com.nedware.Backend.service;

import com.nedware.Backend.domain.dto.ReviewDto;

import java.util.List;

public interface ReviewService {
    ReviewDto addReview(Long dishId, ReviewDto reviewDto, String username);
    List<ReviewDto> getReviewsByDishId(Long dishId);
}


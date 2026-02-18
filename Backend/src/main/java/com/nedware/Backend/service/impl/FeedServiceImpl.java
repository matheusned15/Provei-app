package com.nedware.Backend.service.impl;

import com.nedware.Backend.domain.Review;
import com.nedware.Backend.domain.dto.ReviewDto;
import com.nedware.Backend.repository.ReviewRepository;
import com.nedware.Backend.service.FeedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedServiceImpl implements FeedService {

    private ReviewRepository reviewRepository;

    public FeedServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Page<ReviewDto> getFeedPosts(Pageable pageable) {
        // Get all reviews ordered by date desc with pagination
        return reviewRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapToDto);
    }

    private ReviewDto mapToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setPhotoUrl(review.getPhotoUrl());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setDishId(review.getDish().getId());

        if (review.getUser() != null) {
            dto.setUserId(review.getUser().getId());
            dto.setUserName(review.getUser().getName());
            dto.setUserAvatar(review.getUser().getAvatar());
        }

        return dto;
    }
}

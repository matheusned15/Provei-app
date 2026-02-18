package com.nedware.Backend.service.impl;

import com.nedware.Backend.domain.Dish;
import com.nedware.Backend.domain.Review;
import com.nedware.Backend.domain.User;
import com.nedware.Backend.domain.dto.ReviewDto;
import com.nedware.Backend.repository.DishRepository;
import com.nedware.Backend.repository.ReviewRepository;
import com.nedware.Backend.repository.UserRepository;
import com.nedware.Backend.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private ReviewRepository reviewRepository;
    private DishRepository dishRepository;
    private UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, DishRepository dishRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ReviewDto addReview(Long dishId, ReviewDto reviewDto, String userEmail) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new RuntimeException("Dish not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = new Review();
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        review.setPhotoUrl(reviewDto.getPhotoUrl());
        review.setDish(dish);
        review.setUser(user);

        Review savedReview = reviewRepository.save(review);

        // Update Dish Rating
        updateDishRating(dish);

        return mapToDto(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsByDishId(Long dishId) {
        List<Review> reviews = reviewRepository.findByDishId(dishId);
        return reviews.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private void updateDishRating(Dish dish) {
        List<Review> reviews = reviewRepository.findByDishId(dish.getId());
        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        dish.setAverageRating(average);
        dishRepository.save(dish);
        // Also update Restaurant aggregate rating if needed? For now simple Dish rating.
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


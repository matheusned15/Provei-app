package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.ReviewDto;
import com.nedware.Backend.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/dish/{dishId}")
    public ResponseEntity<ReviewDto> addReview(@PathVariable Long dishId,
                                               @RequestBody ReviewDto reviewDto,
                                               Authentication authentication){
        String username = authentication.getName();
        return new ResponseEntity<>(reviewService.addReview(dishId, reviewDto, username), HttpStatus.CREATED);
    }

    @GetMapping("/dish/{dishId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByDish(@PathVariable Long dishId){
        return ResponseEntity.ok(reviewService.getReviewsByDishId(dishId));
    }
}

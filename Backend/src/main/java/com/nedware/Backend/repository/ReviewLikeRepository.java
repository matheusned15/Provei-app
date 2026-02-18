package com.nedware.Backend.repository;

import com.nedware.Backend.domain.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByUserIdAndReviewId(Long userId, Long reviewId);
    long countByReviewId(Long reviewId);
    boolean existsByUserIdAndReviewId(Long userId, Long reviewId);
}


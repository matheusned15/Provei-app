package com.nedware.Backend.repository;

import com.nedware.Backend.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByDishId(Long dishId);
    List<Review> findByUserId(Long userId);
    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable); // For Feed
}

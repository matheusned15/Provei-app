package com.nedware.Backend.repository;

import com.nedware.Backend.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByReviewIdOrderByCreatedAtAsc(Long reviewId);
}

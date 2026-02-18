package com.nedware.Backend.service.impl;

import com.nedware.Backend.domain.Comment;
import com.nedware.Backend.domain.Review;
import com.nedware.Backend.domain.ReviewLike;
import com.nedware.Backend.domain.User;
import com.nedware.Backend.domain.dto.CommentDto;
import com.nedware.Backend.repository.CommentRepository;
import com.nedware.Backend.repository.ReviewLikeRepository;
import com.nedware.Backend.repository.ReviewRepository;
import com.nedware.Backend.repository.UserRepository;
import com.nedware.Backend.service.SocialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SocialServiceImpl implements SocialService {

    private ReviewLikeRepository reviewLikeRepository;
    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private ReviewRepository reviewRepository;

    public SocialServiceImpl(ReviewLikeRepository reviewLikeRepository, CommentRepository commentRepository, UserRepository userRepository, ReviewRepository reviewRepository) {
        this.reviewLikeRepository = reviewLikeRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional
    public boolean toggleLike(Long reviewId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<ReviewLike> existingLike = reviewLikeRepository.findByUserIdAndReviewId(user.getId(), reviewId);

        if (existingLike.isPresent()) {
            reviewLikeRepository.delete(existingLike.get());
            return false; // Unliked
        } else {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new RuntimeException("Review not found"));
            ReviewLike like = new ReviewLike();
            like.setUser(user);
            like.setReview(review);
            reviewLikeRepository.save(like);
            return true; // Liked
        }
    }

    @Override
    @Transactional
    public CommentDto addComment(Long reviewId, String content, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setReview(review);
        comment.setContent(content);

        Comment savedComment = commentRepository.save(comment);
        return mapToDto(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getComments(Long reviewId) {
        return commentRepository.findByReviewIdOrderByCreatedAtAsc(reviewId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikesCount(Long reviewId) {
        return reviewLikeRepository.countByReviewId(reviewId);
    }

    private CommentDto mapToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUserName(comment.getUser().getName());
        dto.setUserAvatar(comment.getUser().getAvatar());
        dto.setReviewId(comment.getReview().getId());
        dto.setContent(comment.getContent());
        dto.setTimestamp(comment.getCreatedAt());
        return dto;
    }
}

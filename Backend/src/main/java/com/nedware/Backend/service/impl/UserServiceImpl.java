package com.nedware.Backend.service.impl;

import com.nedware.Backend.domain.User;
import com.nedware.Backend.domain.dto.ReviewDto;
import com.nedware.Backend.domain.dto.UserProfileDto;
import com.nedware.Backend.repository.ReviewRepository;
import com.nedware.Backend.repository.UserRepository;
import com.nedware.Backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private ReviewRepository reviewRepository;

    public UserServiceImpl(UserRepository userRepository, ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getCurrentUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDto(user);
    }

    @Override
    @Transactional
    public void followUser(Long userId, String followerEmail) {
        User follower = userRepository.findByEmail(followerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User target = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!follower.getFollowingIds().contains(target.getId())) {
            follower.getFollowingIds().add(target.getId());
            target.getFollowerIds().add(follower.getId());

            userRepository.save(follower);
            userRepository.save(target);
        }
    }

    private UserProfileDto mapToDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setBio(user.getBio());
        dto.setFollowersCount(user.getFollowerIds().size());
        dto.setFollowingCount(user.getFollowingIds().size());

        // Map recent reviews (limit to 5 for profile summary)
        if (user.getReviews() != null) {
            dto.setRecentReviews(user.getReviews().stream()
                    .limit(5)
                    .map(review -> {
                        // Simplify logic or duplicate mapper from ReviewService if needed
                        // For now simplified inline mapping
                        ReviewDto rDto = new ReviewDto();
                        rDto.setId(review.getId());
                        rDto.setRating(review.getRating());
                        rDto.setComment(review.getComment());
                        rDto.setPhotoUrl(review.getPhotoUrl());
                        rDto.setCreatedAt(review.getCreatedAt());
                        rDto.setDishId(review.getDish().getId());
                        return rDto;
                    })
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
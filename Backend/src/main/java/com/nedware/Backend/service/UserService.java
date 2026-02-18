package com.nedware.Backend.service;


import com.nedware.Backend.domain.dto.UserProfileDto;

public interface UserService {
    UserProfileDto getUserProfile(Long id);
    UserProfileDto getCurrentUserProfile(String email);
    void followUser(Long userId, String followerEmail);
}


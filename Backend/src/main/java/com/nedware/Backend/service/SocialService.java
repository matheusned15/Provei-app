package com.nedware.Backend.service;

import com.nedware.Backend.domain.dto.CommentDto;

import java.util.List;

public interface SocialService {
    boolean toggleLike(Long reviewId, String userEmail);
    CommentDto addComment(Long reviewId, String content, String userEmail);
    List<CommentDto> getComments(Long reviewId);
    long getLikesCount(Long reviewId);
}

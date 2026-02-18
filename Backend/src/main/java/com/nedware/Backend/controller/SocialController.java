package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.CommentDto;
import com.nedware.Backend.service.SocialService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/social")
public class SocialController {

    private SocialService socialService;

    public SocialController(SocialService socialService) {
        this.socialService = socialService;
    }

    @PostMapping("/reviews/{reviewId}/like")
    public ResponseEntity<String> toggleLike(@PathVariable Long reviewId, Authentication authentication){
        String username = authentication.getName();
        boolean liked = socialService.toggleLike(reviewId, username);
        return ResponseEntity.ok(liked ? "Review liked" : "Review unliked");
    }

    @PostMapping("/reviews/{reviewId}/comment")
    public ResponseEntity<CommentDto> addComment(@PathVariable Long reviewId, @RequestBody Map<String, String> payload, Authentication authentication){
        String username = authentication.getName();
        String content = payload.get("content");
        return ResponseEntity.ok(socialService.addComment(reviewId, content, username));
    }

    @GetMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long reviewId){
        return ResponseEntity.ok(socialService.getComments(reviewId));
    }

    @GetMapping("/reviews/{reviewId}/likes/count")
    public ResponseEntity<Long> getLikesCount(@PathVariable Long reviewId){
        return ResponseEntity.ok(socialService.getLikesCount(reviewId));
    }
}

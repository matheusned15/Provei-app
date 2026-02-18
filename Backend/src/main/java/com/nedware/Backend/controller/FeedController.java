package com.nedware.Backend.controller;

import com.nedware.Backend.domain.dto.ReviewDto;
import com.nedware.Backend.service.FeedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.web.PageableDefault;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping
    public ResponseEntity<Page<ReviewDto>> getFeed(@PageableDefault(size = 10) Pageable pageable){
        return ResponseEntity.ok(feedService.getFeedPosts(pageable));
    }
}
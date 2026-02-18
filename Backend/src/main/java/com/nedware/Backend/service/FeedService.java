package com.nedware.Backend.service;

import com.nedware.Backend.domain.dto.ReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FeedService {
    Page<ReviewDto> getFeedPosts(Pageable pageable);
}

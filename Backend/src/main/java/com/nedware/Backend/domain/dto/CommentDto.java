package com.nedware.Backend.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long reviewId;
    private String content;
    private LocalDateTime timestamp;
}
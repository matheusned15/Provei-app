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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = SocialServiceImpl.class)
class SocialServiceImplTest {

    @Autowired
    private SocialServiceImpl service;

    @MockitoBean
    private ReviewLikeRepository reviewLikeRepository;
    @MockitoBean private CommentRepository commentRepository;
    @MockitoBean private UserRepository userRepository;
    @MockitoBean private ReviewRepository reviewRepository;

    // ------------------ HELPERS ------------------

    private User mockUser(Long id, String email, String name, String avatar) {
        User u = mock(User.class);
        when(u.getId()).thenReturn(id);
        when(u.getEmail()).thenReturn(email);
        when(u.getName()).thenReturn(name);
        when(u.getAvatar()).thenReturn(avatar);
        return u;
    }

    private Review mockReview(Long id) {
        Review r = mock(Review.class);
        when(r.getId()).thenReturn(id);
        return r;
    }

    private Comment mockComment(Long id, User user, Review review, String content, LocalDateTime createdAt) {
        Comment c = mock(Comment.class);
        when(c.getId()).thenReturn(id);
        when(c.getUser()).thenReturn(user);
        when(c.getReview()).thenReturn(review);
        when(c.getContent()).thenReturn(content);
        when(c.getCreatedAt()).thenReturn(createdAt);
        return c;
    }

    // ------------------ toggleLike() ------------------

    @Test
    @DisplayName("toggleLike: adiciona curtida quando não existe Like")
    void toggleLike_addsWhenNotExists() {
        User user = mockUser(10L, "me@x.com", "Me", "ava.png");
        Review review = mockReview(50L);

        when(userRepository.findByEmail("me@x.com")).thenReturn(Optional.of(user));
        when(reviewLikeRepository.findByUserIdAndReviewId(10L, 50L)).thenReturn(Optional.empty());
        when(reviewRepository.findById(50L)).thenReturn(Optional.of(review));

        boolean liked = service.toggleLike(50L, "me@x.com");

        assertTrue(liked);

        ArgumentCaptor<ReviewLike> captor = ArgumentCaptor.forClass(ReviewLike.class);
        verify(reviewLikeRepository).save(captor.capture());
        ReviewLike saved = captor.getValue();
        assertSame(user, saved.getUser());
        assertSame(review, saved.getReview());
    }

    @Test
    @DisplayName("toggleLike: remove curtida quando Like já existe")
    void toggleLike_removesWhenExists() {
        User user = mockUser(10L, "me@x.com", "Me", "ava.png");
        ReviewLike like = mock(ReviewLike.class);

        when(userRepository.findByEmail("me@x.com")).thenReturn(Optional.of(user));
        when(reviewLikeRepository.findByUserIdAndReviewId(10L, 30L)).thenReturn(Optional.of(like));

        boolean liked = service.toggleLike(30L, "me@x.com");

        assertFalse(liked);
        verify(reviewLikeRepository).delete(like);
        verify(reviewRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("toggleLike: lança quando usuário não existe")
    void toggleLike_userNotFound() {
        when(userRepository.findByEmail("nope@x.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.toggleLike(10L, "nope@x.com"));
        assertTrue(ex.getMessage().contains("User not found"));

        verify(reviewLikeRepository, never()).save(any());
        verify(reviewLikeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("toggleLike: lança quando review não existe ao criar Like")
    void toggleLike_reviewNotFoundOnCreate() {
        User user = mockUser(10L, "me@x.com", "Me", "ava");
        when(userRepository.findByEmail("me@x.com")).thenReturn(Optional.of(user));
        when(reviewLikeRepository.findByUserIdAndReviewId(10L, 999L)).thenReturn(Optional.empty());
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.toggleLike(999L, "me@x.com"));
        assertTrue(ex.getMessage().contains("Review not found"));
    }

    // ------------------ addComment() ------------------

    @Test
    @DisplayName("addComment: cria comentário e mapeia DTO corretamente")
    void addComment_ok() {
        User user = mockUser(1L, "me@x.com", "Me", "ava.png");
        Review review = mockReview(2L);
        Comment saved = mockComment(5L, user, review, "Olá!", LocalDateTime.now());

        when(userRepository.findByEmail("me@x.com")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(2L)).thenReturn(Optional.of(review));
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        CommentDto dto = service.addComment(2L, "Olá!", "me@x.com");

        assertEquals(5L, dto.getId());
        assertEquals(1L, dto.getUserId());
        assertEquals("Me", dto.getUserName());
        assertEquals("ava.png", dto.getUserAvatar());
        assertEquals(2L, dto.getReviewId());
        assertEquals("Olá!", dto.getContent());
        assertNotNull(dto.getTimestamp());
    }

    @Test
    @DisplayName("addComment: lança quando usuário não existe")
    void addComment_userNotFound() {
        when(userRepository.findByEmail("none@x.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.addComment(2L, "c", "none@x.com"));
        assertTrue(ex.getMessage().contains("User not found"));

        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("addComment: lança quando review não existe")
    void addComment_reviewNotFound() {
        User user = mockUser(1L, "me@x.com", "Me", "ava");
        when(userRepository.findByEmail("me@x.com")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.addComment(999L, "c", "me@x.com"));
        assertTrue(ex.getMessage().contains("Review not found"));
    }

    // ------------------ getComments() ------------------

    @Test
    @DisplayName("getComments: retorna lista de DTOs mapeada")
    void getComments_ok() {
        User user = mockUser(10L, "a@a.com", "A", "ava");
        Review review = mockReview(5L);
        Comment c1 = mockComment(1L, user, review, "c1", LocalDateTime.now());
        Comment c2 = mockComment(2L, user, review, "c2", LocalDateTime.now());

        when(commentRepository.findByReviewIdOrderByCreatedAtAsc(5L)).thenReturn(List.of(c1, c2));

        List<CommentDto> dtos = service.getComments(5L);

        assertEquals(2, dtos.size());
        assertEquals("c1", dtos.get(0).getContent());
        assertEquals("c2", dtos.get(1).getContent());
    }

    // ------------------ getLikesCount() ------------------

    @Test
    @DisplayName("getLikesCount: retorna contagem correta")
    void getLikesCount_ok() {
        when(reviewLikeRepository.countByReviewId(77L)).thenReturn(12L);

        long count = service.getLikesCount(77L);

        assertEquals(12L, count);
        verify(reviewLikeRepository).countByReviewId(77L);
    }
}

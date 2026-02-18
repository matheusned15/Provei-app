package com.nedware.Backend.service.impl;


import com.nedware.Backend.domain.User;
import com.nedware.Backend.domain.dto.ReviewDto;
import com.nedware.Backend.domain.dto.UserProfileDto;
import com.nedware.Backend.repository.ReviewRepository;
import com.nedware.Backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private ReviewRepository reviewRepository;

    @InjectMocks
    private UserServiceImpl service; // classe alvo dos testes

    @BeforeEach
    void setup() {
        // @InjectMocks já instancia com mocks
    }

    // ---------- helpers ----------
    private User mockUser(long id, String email, String name) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        when(user.getEmail()).thenReturn(email);
        when(user.getName()).thenReturn(name);

        // listas reais para permitir add/size()
        List<Long> followers = new ArrayList<>();
        List<Long> following = new ArrayList<>();
        when(user.getFollowerIds()).thenReturn(followers);
        when(user.getFollowingIds()).thenReturn(following);

        // campos simples do perfil
        when(user.getAvatar()).thenReturn("https://cdn/avatar.png");
        when(user.getBio()).thenReturn("bio...");

        return user;
    }

    private Object mockReview(long id, int rating, String comment, String photoUrl, Instant createdAt, long dishId) {
        // mock leve de Review com os getters usados no mapToDto()
        Object review = mock(Object.class, withSettings().name("Review#" + id));
        // stubs por método com thenAnswer via mocks "typed"
        // Para evitar casts, usamos Answer com lambdas e when(...).thenReturn(...)

        // Métodos esperados:
        //  - getId(), getRating(), getComment(), getPhotoUrl(), getCreatedAt(), getDish().getId()
        // Como não temos classes, usamos leniência com Mockito + deep stubs para dish
        Object dish = mock(Object.class, withSettings().name("Dish#" + dishId));
        try {
            when(review.getClass().getMethod("getId")).thenThrow(new NoSuchMethodException());
        } catch (Exception ignore) {}

        // Em vez de reflection, criamos interfaces por mocks encadeados:
        // Vamos simular via Answer do Mockito com Default Answer para métodos:
        // -> Para simplificar no JUnit, criamos mocks com "lenient when" usando padrão de nomes:
        // Usaremos doReturn nas "invocações" dos métodos via Mockito "spy" não é viável sem classe.
        // Solução prática: criar proxies simples com Map de propriedades.

        // ---- IMPLEMENTAÇÃO SIMPLES via Map + Answer ----
        Map<String, Object> props = new HashMap<>();
        props.put("id", id);
        props.put("rating", rating);
        props.put("comment", comment);
        props.put("photoUrl", photoUrl);
        props.put("createdAt", createdAt);

        // mock de dish com getId():
        Object dishMock = mock(Object.class, withSettings().defaultAnswer(invocation -> {
            if ("getId".equals(invocation.getMethod().getName())) return dishId;
            return RETURNS_DEFAULTS.answer(invocation);
        }));
        // review com getters esperados:
        Object reviewMock = mock(Object.class, withSettings().defaultAnswer(invocation -> {
            String m = invocation.getMethod().getName();
            switch (m) {
                case "getId": return props.get("id");
                case "getRating": return props.get("rating");
                case "getComment": return props.get("comment");
                case "getPhotoUrl": return props.get("photoUrl");
                case "getCreatedAt": return props.get("createdAt");
                case "getDish": return dishMock;
                default: return RETURNS_DEFAULTS.answer(invocation);
            }
        }));
        return reviewMock;
    }

    @Test
    @DisplayName("getUserProfile: retorna DTO mapeado e limita recentReviews a 5")
    void getUserProfile_ok_withReviewsLimitedTo5() {
        User user = mockUser(10L, "x@x.com", "X");

        // followers/following para contagem
        user.getFollowerIds().addAll(Arrays.asList(2L, 3L, 4L));
        user.getFollowingIds().addAll(Arrays.asList(100L, 101L));

        // lista de 6 reviews -> mapToDto deve limitar para 5
        List<Object> reviews = new ArrayList<>();
        IntStream.rangeClosed(1, 6).forEach(i ->
                reviews.add(mockReview(i, 5, "c"+i, "p"+i, Instant.now(), i))
        );
        when(user.getReviews()).thenReturn((List) reviews);

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        UserProfileDto dto = service.getUserProfile(10L); // alvo
        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("X", dto.getName());
        assertEquals("x@x.com", dto.getEmail());
        assertEquals(3, dto.getFollowersCount());
        assertEquals(2, dto.getFollowingCount());
        assertNotNull(dto.getRecentReviews());
        assertEquals(5, dto.getRecentReviews().size(), "Deve limitar a 5 reviews");

        // valida mapeamento básico de um review
        ReviewDto r0 = dto.getRecentReviews().get(0);
        assertNotNull(r0.getId());
        assertNotNull(r0.getRating());
        assertNotNull(r0.getCreatedAt());
    }

    @Test
    @DisplayName("getUserProfile: lança quando não encontra")
    void getUserProfile_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getUserProfile(99L));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    @DisplayName("getCurrentUserProfile: ok e mapeia com reviews == null")
    void getCurrentUserProfile_ok_reviewsNull() {
        User user = mockUser(1L, "me@site.com", "Me");
        // reviews == null cobre ramo condicional no mapToDto
        when(user.getReviews()).thenReturn(null);
        when(userRepository.findByEmail("me@site.com")).thenReturn(Optional.of(user));

        UserProfileDto dto = service.getCurrentUserProfile("me@site.com");
        assertNotNull(dto);
        assertEquals("Me", dto.getName());
        assertNull(dto.getRecentReviews(), "Quando reviews é null, recentReviews não deve ser setado");
    }

    @Test
    @DisplayName("getCurrentUserProfile: lança quando não encontra por email")
    void getCurrentUserProfile_notFound() {
        when(userRepository.findByEmail("unknown@x.com")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getCurrentUserProfile("unknown@x.com"));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    @DisplayName("followUser: adiciona follow quando ainda não segue e salva ambos")
    void followUser_addsAndSaves() {
        User follower = mockUser(11L, "f@x.com", "Follower");
        User target = mockUser(22L, "t@x.com", "Target");

        when(userRepository.findByEmail("f@x.com")).thenReturn(Optional.of(follower));
        when(userRepository.findById(22L)).thenReturn(Optional.of(target));

        service.followUser(22L, "f@x.com");

        assertTrue(follower.getFollowingIds().contains(22L));
        assertTrue(target.getFollowerIds().contains(11L));
        verify(userRepository).save(follower);
        verify(userRepository).save(target);
    }

    @Test
    @DisplayName("followUser: não faz nada quando já segue")
    void followUser_noopWhenAlreadyFollowing() {
        User follower = mockUser(11L, "f@x.com", "Follower");
        User target = mockUser(22L, "t@x.com", "Target");

        follower.getFollowingIds().add(22L); // já segue
        target.getFollowerIds().add(11L);

        when(userRepository.findByEmail("f@x.com")).thenReturn(Optional.of(follower));
        when(userRepository.findById(22L)).thenReturn(Optional.of(target));

        service.followUser(22L, "f@x.com");

        // não deve salvar redundante (pode até salvar, mas vamos garantir que não adicionou duplicado)
        assertEquals(1, follower.getFollowingIds().size());
        assertEquals(1, target.getFollowerIds().size());
        verify(userRepository, never()).save(any()); // se preferir permitir save, remova esta asserção
    }
}

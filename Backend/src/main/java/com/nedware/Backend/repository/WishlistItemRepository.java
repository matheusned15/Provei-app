package com.nedware.Backend.repository;

import com.nedware.Backend.domain.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUserId(Long userId);
    Optional<WishlistItem> findByUserIdAndDishId(Long userId, Long dishId);
    void deleteByUserIdAndDishId(Long userId, Long dishId);
}

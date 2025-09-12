package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.Wishlist;
import com.exe201.group1.psgp_be.models.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishListItemRepo extends JpaRepository<WishlistItem, Integer> {
    Optional<WishlistItem> findByProductIdAndWishlistId(int productId, int wishlistId);

    void removeAllByWishlist(Wishlist wishlist);

    List<WishlistItem> findAllByWishlist(Wishlist wishlist);
}

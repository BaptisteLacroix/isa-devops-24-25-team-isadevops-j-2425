package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.CartItem;

import java.time.LocalDateTime;

public record CartItemAddItemToCartDTO(
        int quantity,
        LocalDateTime startTime,
        long itemId
) {
    public CartItemAddItemToCartDTO(CartItem entity) {
        this(
                entity.getQuantity(),
                entity.getStartTime(),
                entity.getItem().getItemId()
        );
    }
}

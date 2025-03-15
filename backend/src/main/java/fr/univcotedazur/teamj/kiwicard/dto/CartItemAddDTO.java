package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.CartItem;

import java.time.LocalDateTime;

public record CartItemAddDTO(
        int quantity,
        LocalDateTime startTime,
        long itemId
) {
    public CartItemAddDTO(CartItem entity) {
        this(
                entity.getQuantity(),
                entity.getStartTime(),
                entity.getItem().getItemId()
        );
    }
}

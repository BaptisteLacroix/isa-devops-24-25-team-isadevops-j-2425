package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.CartItem;

import java.time.LocalDateTime;

public record CartItemDTO(
        Long cartItemId,
        int quantity,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long itemId
) {
    public CartItemDTO(CartItem entity) {
        this(entity.getCartItemId(), entity.getQuantity(), entity.getStartTime(), entity.getEndTime(), entity.getItem().getItemId());
    }
}

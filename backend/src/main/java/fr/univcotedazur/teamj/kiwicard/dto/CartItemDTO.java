package fr.univcotedazur.teamj.kiwicard.dto;

import java.time.LocalDateTime;

public record CartItemDTO(
        Long cartItemId,
        int quantity,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long itemId
) {
    public CartItemDTO(fr.univcotedazur.teamj.kiwicard.entities.CartItem entity) {
        this(entity.getCartItemId(), entity.getQuantity(), entity.getStartTime(), entity.getEndTime(), entity.getItem().getItemId());
    }
}

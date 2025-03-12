package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.CartItem;

import java.time.LocalDateTime;

public record CartItemDTO(
        int quantity,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ItemDTO item
) {
    public CartItemDTO(CartItem entity) {
        this(
                entity.getQuantity(),
                entity.getStartTime(),
                entity.getEndTime(),
                new ItemDTO(entity.getItem())
        );
    }
}

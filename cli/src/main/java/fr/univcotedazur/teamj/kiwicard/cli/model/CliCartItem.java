package fr.univcotedazur.teamj.kiwicard.cli.model;

import java.time.LocalDateTime;

public record CliCartItem(
        int quantity,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long itemId
) {
    @Override
    public String toString() {
        return String.format("Item ID: %d, Quantity: %d, Time: [%s - %s]",
                itemId(), quantity(),
                startTime() != null ? startTime().toString() : "N/A",
                endTime() != null ? endTime().toString() : "N/A");
    }
}

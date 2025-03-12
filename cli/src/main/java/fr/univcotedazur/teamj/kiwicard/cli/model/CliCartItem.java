package fr.univcotedazur.teamj.kiwicard.cli.model;

import java.time.LocalDateTime;

public record CliCartItem(
        int quantity,
        LocalDateTime startTime,
        LocalDateTime endTime,
        CliItem item
) {
    @Override
    public String toString() {
        return String.format("Item %s, Quantity: %d, Time: [%s - %s]",
                item(), quantity(),
                startTime() != null ? startTime().toString() : "N/A",
                endTime() != null ? endTime().toString() : "N/A");
    }
}

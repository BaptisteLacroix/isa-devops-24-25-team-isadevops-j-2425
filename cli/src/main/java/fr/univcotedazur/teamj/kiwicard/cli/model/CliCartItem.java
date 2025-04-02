package fr.univcotedazur.teamj.kiwicard.cli.model;

import java.time.LocalDateTime;

public record CliCartItem(
        int quantity,
        LocalDateTime startTime,
        CliItem item
) {
    @Override
    public String toString() {
        return String.format("Article %s, Quantité: %d %s",
                item(), quantity(),
                startTime() != null ? startTime().toString() : "");
    }
}

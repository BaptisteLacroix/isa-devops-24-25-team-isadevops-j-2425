package fr.univcotedazur.teamj.kiwicard.cli.model;

import java.time.LocalDateTime;

public record CliCartItem(
        int quantity,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long itemId
) {
}
